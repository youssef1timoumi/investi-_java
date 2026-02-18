import { useState } from 'react';
import type { Comment } from '../types';
import VoteButtons from './VoteButtons';
import styles from './CommentList.module.css';

interface CommentListProps {
  comments: Comment[];
  onVote: (commentId: string, direction: 'up' | 'down') => void;
}

interface CommentVoteState {
  [commentId: string]: 'up' | 'down' | null;
}

interface CommentItemProps {
  comment: Comment;
  userVote: 'up' | 'down' | null;
  onVote: (commentId: string, direction: 'up' | 'down') => void;
  depth: number;
}

function CommentItem({ comment, userVote, onVote, depth }: CommentItemProps) {
  const maxDepth = 4;
  const currentDepth = Math.min(depth, maxDepth);

  const handleUpvote = () => {
    onVote(comment.id, 'up');
  };

  const handleDownvote = () => {
    onVote(comment.id, 'down');
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  return (
    <article
      className={styles.commentItem}
      style={{ '--depth': currentDepth } as React.CSSProperties}
      data-depth={currentDepth}
      aria-label={`Comment by ${comment.authorName}`}
    >
      <div className={styles.commentVotes}>
        <VoteButtons
          upvotes={comment.upvotes}
          downvotes={comment.downvotes}
          userVote={userVote}
          onUpvote={handleUpvote}
          onDownvote={handleDownvote}
        />
      </div>
      <div className={styles.commentContent}>
        <header className={styles.commentHeader}>
          <span className={styles.authorName}>{comment.authorName}</span>
          <span className={styles.separator}>•</span>
          <time className={styles.timestamp} dateTime={comment.createdAt}>
            {formatDate(comment.createdAt)}
          </time>
        </header>
        <p className={styles.commentText}>{comment.content}</p>
      </div>
    </article>
  );
}

export default function CommentList({ comments, onVote }: CommentListProps) {
  const [voteStates, setVoteStates] = useState<CommentVoteState>({});

  const handleVote = (commentId: string, direction: 'up' | 'down') => {
    setVoteStates((prev) => {
      const currentVote = prev[commentId];
      // Toggle off if clicking the same direction, otherwise set new direction
      const newVote = currentVote === direction ? null : direction;
      return { ...prev, [commentId]: newVote };
    });
    onVote(commentId, direction);
  };

  // Build nested comment structure from flat array
  const buildCommentTree = (comments: Comment[]): Comment[] => {
    const commentMap = new Map<string, Comment>();
    const rootComments: Comment[] = [];

    // First pass: create a map of all comments with empty replies arrays
    comments.forEach((comment) => {
      commentMap.set(comment.id, { ...comment, replies: [] });
    });

    // Second pass: build the tree structure
    comments.forEach((comment) => {
      const commentWithReplies = commentMap.get(comment.id)!;
      if (comment.parentId) {
        const parent = commentMap.get(comment.parentId);
        if (parent) {
          parent.replies = parent.replies || [];
          parent.replies.push(commentWithReplies);
        } else {
          // Parent not found, treat as root comment
          rootComments.push(commentWithReplies);
        }
      } else {
        rootComments.push(commentWithReplies);
      }
    });

    return rootComments;
  };

  const renderComments = (comments: Comment[], depth: number = 0): React.ReactNode => {
    return comments.map((comment) => (
      <div key={comment.id} className={styles.commentThread}>
        <CommentItem
          comment={comment}
          userVote={voteStates[comment.id] || null}
          onVote={handleVote}
          depth={depth}
        />
        {comment.replies && comment.replies.length > 0 && (
          <div className={styles.replies} role="group" aria-label="Replies">
            {renderComments(comment.replies, depth + 1)}
          </div>
        )}
      </div>
    ));
  };

  const nestedComments = buildCommentTree(comments);

  if (comments.length === 0) {
    return (
      <section className={styles.commentList} aria-label="Comments">
        <p className={styles.emptyState}>No comments yet. Be the first to comment!</p>
      </section>
    );
  }

  return (
    <section className={styles.commentList} aria-label="Comments">
      <h3 className={styles.commentsTitle}>
        {comments.length} {comments.length === 1 ? 'Comment' : 'Comments'}
      </h3>
      <div className={styles.commentsContainer}>
        {renderComments(nestedComments)}
      </div>
    </section>
  );
}
