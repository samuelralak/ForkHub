/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mobile.ui.comment;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R.id;
import com.github.mobile.R.layout;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.HttpImageGetter;
import com.github.mobile.util.TimeUtils;

import org.eclipse.egit.github.core.Comment;
import static com.github.mobile.Intents.EXTRA_COMMENT;

/**
 * Adapter for a list of {@link Comment} objects
 */
public class CommentListAdapter extends SingleTypeAdapter<Comment> {

    private final AvatarLoader avatars;

    private final HttpImageGetter imageGetter;

    /**
     * Callback listener to be invoked when user tries to edit a comment.
     */
    private final EditCommentListener editCommentListener;

    /**
     * Callback listener to be invoked when user tries to edit a comment.
     */
    private final DeleteCommentListener deleteCommentListener;

    /**
     * Create list adapter
     *
     * @param inflater
     * @param elements
     * @param avatars
     * @param imageGetter
     */
    public CommentListAdapter(LayoutInflater inflater, Comment[] elements,
            AvatarLoader avatars, HttpImageGetter imageGetter) {
        this(inflater, elements, avatars, imageGetter, null, null);
    }

    /**
     * Create list adapter
     *
     * @param inflater
     * @param avatars
     * @param imageGetter
     */
    public CommentListAdapter(LayoutInflater inflater, AvatarLoader avatars,
            HttpImageGetter imageGetter) {
        this(inflater, null, avatars, imageGetter);
    }

    /**
     * Create list adapter
     *
     * @param inflater
     * @param elements
     * @param avatars
     * @param imageGetter
     */
    public CommentListAdapter(LayoutInflater inflater, Comment[] elements,
            AvatarLoader avatars, HttpImageGetter imageGetter,
            EditCommentListener editCommentListener, DeleteCommentListener deleteCommentListener) {
        super(inflater, layout.comment_item);

        this.avatars = avatars;
        this.imageGetter = imageGetter;
        this.editCommentListener = editCommentListener;
        this.deleteCommentListener = deleteCommentListener;
        setItems(elements);
    }

    @Override
    protected void update(int position, final Comment comment) {
        imageGetter.bind(textView(0), comment.getBodyHtml(), comment.getId());
        avatars.bind(imageView(3), comment.getUser());

        setText(1, comment.getUser().getLogin());
        setText(2, TimeUtils.getRelativeTime(comment.getUpdatedAt()));

        // Edit Comment ImageButton
        final ImageView ivEdit =  view(4);
        if (editCommentListener != null) {
            ivEdit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editCommentListener.onEditComment(comment);
                }
            });
        } else {
            ivEdit.setVisibility(View.GONE);
        }

        // Delete Comment ImageButton
        final ImageView ivDelete =  view(5);
        if (deleteCommentListener != null) {
            ivDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCommentListener.onDeleteComment(comment);
                }
            });
        } else {
            ivDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).getId();
    }

    @Override
    protected View initialize(View view) {
        view = super.initialize(view);

        textView(view, 0).setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { id.tv_comment_body, id.tv_comment_author,
                id.tv_comment_date, id.iv_avatar, id.iv_comment_edit, id.iv_comment_delete };
    }
}
