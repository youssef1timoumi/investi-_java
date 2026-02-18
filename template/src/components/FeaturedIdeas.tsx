import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getIdeas } from '../data/mockData';
import Button from './Button';
import styles from './FeaturedIdeas.module.css';

type FilterType = 'all' | 'trending' | 'new' | 'funded';

export default function FeaturedIdeas() {
  const [activeFilter, setActiveFilter] = useState<FilterType>('all');
  const [hoveredId, setHoveredId] = useState<string | null>(null);
  const navigate = useNavigate();
  const ideas = getIdeas();

  const filters: { id: FilterType; label: string }[] = [
    { id: 'all', label: 'All Ideas' },
    { id: 'trending', label: '🔥 Trending' },
    { id: 'new', label: '✨ New' },
    { id: 'funded', label: '💰 Funded' },
  ];

  const filteredIdeas = ideas.filter(idea => {
    switch (activeFilter) {
      case 'trending':
        return idea.investorCount >= 5;
      case 'new':
        return idea.status === 'open';
      case 'funded':
        return idea.status === 'funded' || idea.status === 'project';
      default:
        return true;
    }
  }).slice(0, 4);

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'open':
        return { label: 'Open', className: styles.statusOpen };
      case 'in-collaboration':
        return { label: 'In Collaboration', className: styles.statusCollab };
      case 'funded':
        return { label: 'Funded', className: styles.statusFunded };
      case 'project':
        return { label: 'Active Project', className: styles.statusProject };
      default:
        return { label: status, className: '' };
    }
  };

  return (
    <div className={styles.container}>
      {/* Filter Tabs */}
      <div className={styles.filters} role="tablist" aria-label="Filter ideas">
        {filters.map(filter => (
          <button
            key={filter.id}
            className={`${styles.filterButton} ${activeFilter === filter.id ? styles.activeFilter : ''}`}
            onClick={() => setActiveFilter(filter.id)}
            role="tab"
            aria-selected={activeFilter === filter.id}
            aria-controls="ideas-panel"
          >
            {filter.label}
          </button>
        ))}
      </div>

      {/* Ideas Grid */}
      <div 
        id="ideas-panel"
        className={styles.grid}
        role="tabpanel"
        aria-label={`${activeFilter} ideas`}
      >
        {filteredIdeas.map((idea, index) => {
          const status = getStatusBadge(idea.status);
          return (
            <article
              key={idea.id}
              className={`${styles.ideaCard} ${hoveredId === idea.id ? styles.hovered : ''}`}
              style={{ animationDelay: `${index * 0.1}s` }}
              onMouseEnter={() => setHoveredId(idea.id)}
              onMouseLeave={() => setHoveredId(null)}
              onClick={() => navigate('/investor')}
              role="button"
              tabIndex={0}
              onKeyDown={(e) => e.key === 'Enter' && navigate('/investor')}
              aria-label={`${idea.title} by ${idea.authorName}`}
            >
              <div className={styles.cardHeader}>
                <span className={`${styles.statusBadge} ${status.className}`}>
                  {status.label}
                </span>
                <span className={styles.investorCount}>
                  👥 {idea.investorCount} interested
                </span>
              </div>

              <h3 className={styles.ideaTitle}>{idea.title}</h3>
              <p className={styles.ideaDescription}>{idea.description}</p>

              <div className={styles.tags}>
                {idea.tags.map(tag => (
                  <span key={tag} className={styles.tag}>#{tag}</span>
                ))}
              </div>

              <div className={styles.cardFooter}>
                <span className={styles.author}>
                  <span className={styles.authorAvatar}>
                    {idea.authorName.charAt(0)}
                  </span>
                  {idea.authorName}
                </span>
                <span className={styles.viewMore}>View →</span>
              </div>

              {/* Hover overlay */}
              <div className={styles.hoverOverlay}>
                <span>Click to explore</span>
              </div>
            </article>
          );
        })}
      </div>

      {/* View All Button */}
      <div className={styles.viewAllContainer}>
        <Button 
          variant="primary" 
          size="lg"
          onClick={() => navigate('/investor')}
        >
          Explore All Ideas
        </Button>
      </div>
    </div>
  );
}
