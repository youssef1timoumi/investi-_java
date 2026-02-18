import { useState, useCallback, useMemo } from 'react';
import PageLayout from '../components/PageLayout';
import PostCard from '../components/PostCard';
import CommentList from '../components/CommentList';
import { getPosts, getCommentsByPostId } from '../data/mockData';
import type { Post, Comment } from '../types';
import styles from './Forum.module.css';

interface PostVoteState {
  [postId: string]: {
    userVote: 'up' | 'down' | null;
    upvotes: number;
    downvotes: number;
  };
}

const categories = ['All', 'Tips & Advice', 'Success Stories', 'Investor Insights', 'Collaboration', 'Announcements'];

export default function Forum() {
  const posts = getPosts();
  const [expandedPostId, setExpandedPostId] = useState<string | null>(null);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [sortBy, setSortBy] = useState<'trending' | 'recent' | 'top'>('trending');
  const [postVotes, setPostVotes] = useState<PostVoteState>(() => {
    const initialState: PostVoteState = {};
    posts.forEach((post) => {
      initialState[post.id] = {
        userVote: null,
        upvotes: post.upvotes,
        downvotes: post.downvotes,
      };
    });
    return initialState;
  });

  const filteredAndSortedPosts = useMemo(() => {
    let filtered = posts;
    
    if (selectedCategory !== 'All') {
      filtered = posts.filter(post => post.category === selectedCategory);
    }
    
    return [...filtered].sort((a, b) => {
      if (sortBy === 'trending') {
        const aScore = (a.isTrending ? 1000 : 0) + a.upvotes - a.downvotes;
        const bScore = (b.isTrending ? 1000 : 0) + b.upvotes - b.downvotes;
        return bScore - aScore;
      } else if (sortBy === 'recent') {
        return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
      } else {
        return (b.upvotes - b.downvotes) - (a.upvotes - a.downvotes);
      }
    });
  }, [posts, selectedCategory, sortBy]);

  const handlePostUpvote = useCallback((postId: string) => {
    setPostVotes((prev) => {
      const current = prev[postId];
      if (!current) return prev;

      let newUpvotes = current.upvotes;
      let newDownvotes = current.downvotes;
      let newUserVote: 'up' | 'down' | null;

      if (current.userVote === 'up') {
        newUpvotes -= 1;
        newUserVote = null;
      } else if (current.userVote === 'down') {
        newDownvotes -= 1;
        newUpvotes += 1;
        newUserVote = 'up';
      } else {
        newUpvotes += 1;
        newUserVote = 'up';
      }

      return {
        ...prev,
        [postId]: { userVote: newUserVote, upvotes: newUpvotes, downvotes: newDownvotes },
      };
    });
  }, []);

  const handlePostDownvote = useCallback((postId: string) => {
    setPostVotes((prev) => {
      const current = prev[postId];
      if (!current) return prev;

      let newUpvotes = current.upvotes;
      let newDownvotes = current.downvotes;
      let newUserVote: 'up' | 'down' | null;

      if (current.userVote === 'down') {
        newDownvotes -= 1;
        newUserVote = null;
      } else if (current.userVote === 'up') {
        newUpvotes -= 1;
        newDownvotes += 1;
        newUserVote = 'down';
      } else {
        newDownvotes += 1;
        newUserVote = 'down';
      }

      return {
        ...prev,
        [postId]: { userVote: newUserVote, upvotes: newUpvotes, downvotes: newDownvotes },
      };
    });
  }, []);

  const handleCommentClick = useCallback((postId: string) => {
    setExpandedPostId((prev) => (prev === postId ? null : postId));
  }, []);

  const handleCommentVote = useCallback((commentId: string, direction: 'up' | 'down') => {
    console.log(`Comment ${commentId} voted ${direction}`);
  }, []);

  const getPostWithUpdatedVotes = (post: Post): Post => {
    const voteState = postVotes[post.id];
    if (!voteState) return post;
    return { ...post, upvotes: voteState.upvotes, downvotes: voteState.downvotes };
  };

  const getCommentsForPost = (postId: string): Comment[] => {
    return getCommentsByPostId(postId);
  };

  return (
    <PageLayout backgroundColor="light">
      <div className={styles.forumPage}>
        {/* Hero Section */}
        <div className={styles.heroSection}>
          <div className={styles.heroContent}>
            <h1 className={styles.heroTitle}>Community Forum</h1>
            <p className={styles.heroSubtitle}>
              Connect, share insights, and learn from fellow innovators and investors
            </p>
          </div>
        </div>

        <div className={styles.forumContainer}>
          {/* Sidebar */}
          <aside className={styles.sidebar}>
            <div className={styles.sidebarCard}>
              <h3 className={styles.sidebarTitle}>Categories</h3>
              <ul className={styles.categoryList}>
                {categories.map((category) => (
                  <li key={category}>
                    <button
                      className={`${styles.categoryButton} ${selectedCategory === category ? styles.categoryActive : ''}`}
                      onClick={() => setSelectedCategory(category)}
                    >
                      {category}
                      {category !== 'All' && (
                        <span className={styles.categoryCount}>
                          {posts.filter(p => p.category === category).length}
                        </span>
                      )}
                    </button>
                  </li>
                ))}
              </ul>
            </div>

            <div className={styles.sidebarCard}>
              <h3 className={styles.sidebarTitle}>Forum Stats</h3>
              <div className={styles.statsList}>
                <div className={styles.statItem}>
                  <span className={styles.statIcon}>📝</span>
                  <div>
                    <span className={styles.statValue}>{posts.length}</span>
                    <span className={styles.statLabel}>Posts</span>
                  </div>
                </div>
                <div className={styles.statItem}>
                  <span className={styles.statIcon}>💬</span>
                  <div>
                    <span className={styles.statValue}>{posts.reduce((acc, p) => acc + p.commentCount, 0)}</span>
                    <span className={styles.statLabel}>Comments</span>
                  </div>
                </div>
                <div className={styles.statItem}>
                  <span className={styles.statIcon}>👥</span>
                  <div>
                    <span className={styles.statValue}>{new Set(posts.map(p => p.authorId)).size}</span>
                    <span className={styles.statLabel}>Contributors</span>
                  </div>
                </div>
              </div>
            </div>
          </aside>

          {/* Main Content */}
          <main className={styles.mainContent}>
            {/* Sort Controls */}
            <div className={styles.controls}>
              <div className={styles.sortButtons}>
                <button
                  className={`${styles.sortButton} ${sortBy === 'trending' ? styles.sortActive : ''}`}
                  onClick={() => setSortBy('trending')}
                >
                  🔥 Trending
                </button>
                <button
                  className={`${styles.sortButton} ${sortBy === 'recent' ? styles.sortActive : ''}`}
                  onClick={() => setSortBy('recent')}
                >
                  🕐 Recent
                </button>
                <button
                  className={`${styles.sortButton} ${sortBy === 'top' ? styles.sortActive : ''}`}
                  onClick={() => setSortBy('top')}
                >
                  ⬆️ Top
                </button>
              </div>
              <span className={styles.postCount}>
                {filteredAndSortedPosts.length} {filteredAndSortedPosts.length === 1 ? 'post' : 'posts'}
              </span>
            </div>

            {/* Posts */}
            <div className={styles.postsContainer}>
              {filteredAndSortedPosts.map((post) => {
                const updatedPost = getPostWithUpdatedVotes(post);
                const voteState = postVotes[post.id];
                const isExpanded = expandedPostId === post.id;
                const comments = isExpanded ? getCommentsForPost(post.id) : [];

                return (
                  <article key={post.id} className={styles.postWrapper}>
                    <PostCard
                      post={updatedPost}
                      userVote={voteState?.userVote || null}
                      onUpvote={() => handlePostUpvote(post.id)}
                      onDownvote={() => handlePostDownvote(post.id)}
                      onCommentClick={() => handleCommentClick(post.id)}
                    />
                    
                    {isExpanded && (
                      <div className={styles.commentsSection}>
                        <CommentList comments={comments} onVote={handleCommentVote} />
                      </div>
                    )}
                  </article>
                );
              })}
            </div>

            {filteredAndSortedPosts.length === 0 && (
              <div className={styles.emptyState}>
                <span className={styles.emptyIcon}>📭</span>
                <h3>No posts found</h3>
                <p>Try selecting a different category or be the first to start a discussion!</p>
              </div>
            )}
          </main>
        </div>
      </div>
    </PageLayout>
  );
}
