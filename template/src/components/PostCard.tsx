import type { Post } from '../types';
import Card from './Card';
import VoteButtons from './VoteButtons';
import styles from './PostCard.module.css';

interface PostCardProps {
  post: Post;
  userVote?: 'up' | 'down' | null;
  onUpvote: () => void;
  onDownvote: () => void;
  onCommentClick: () => void;
}

export default function PostCard({
  post,
  userVote = null,
  onUpvote,
  onDownvote,
  onCommentClick,
}: PostCardProps) {
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  return (
    <Card variant="light" className={styles.postCard}>
      <div className={styles.voteColumn}>
        <VoteButtons
          upvotes={post.upvotes}
          downvotes={post.downvotes}
          userVote={userVote}
          onUpvote={onUpvote}
          onDownvote={onDownvote}
        />
      </div>
      
      <div className={styles.contentColumn}>
        <div className={styles.header}>
          <div className={styles.authorInfo}>
            {post.authorAvatar ? (
              <img 
                src={post.authorAvatar} 
                alt={post.authorName}
                className={styles.avatar}
              />
            ) : (
              <div className={styles.avatarPlaceholder}>
                {post.authorName.charAt(0)}
              </div>
            )}
            <div className={styles.meta}>
              <span className={styles.author}>{post.authorName}</span>
              <div className={styles.metaRow}>
                <time className={styles.date} dateTime={post.createdAt}>
                  {formatDate(post.createdAt)}
                </time>
                {post.category && (
                  <>
                    <span className={styles.separator}>•</span>
                    <span className={styles.category}>{post.category}</span>
                  </>
                )}
              </div>
            </div>
          </div>
          {post.isTrending && (
            <span className={styles.trendingBadge}>
              <span className={styles.trendingIcon}>🔥</span>
              Trending
            </span>
          )}
        </div>
        
        <h3 className={styles.title}>{post.title}</h3>
        
        <p className={styles.content}>{post.content}</p>
        
        <div className={styles.actions}>
          <button
            type="button"
            className={styles.commentButton}
            onClick={onCommentClick}
            aria-label={`View ${post.commentCount} comments`}
          >
            <svg
              className={styles.commentIcon}
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              aria-hidden="true"
            >
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
            </svg>
            <span>{post.commentCount} {post.commentCount === 1 ? 'Comment' : 'Comments'}</span>
          </button>
          <button type="button" className={styles.shareButton}>
            <svg className={styles.shareIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8M16 6l-4-4-4 4M12 2v13"/>
            </svg>
            Share
          </button>
          <button type="button" className={styles.bookmarkButton}>
            <svg className={styles.bookmarkIcon} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
            </svg>
            Save
          </button>
        </div>
      </div>
    </Card>
  );
}
