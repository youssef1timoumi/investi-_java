import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import PageLayout from '../components/PageLayout';
import IdeaCard from '../components/IdeaCard';
import { getIdeas } from '../data/mockData';
import styles from './InvestorView.module.css';

interface ToastState {
  visible: boolean;
  ideaTitle: string;
}

export default function InvestorView() {
  const navigate = useNavigate();
  const ideas = getIdeas();
  const [toast, setToast] = useState<ToastState>({ visible: false, ideaTitle: '' });

  const handleLogout = () => {
    navigate('/');
  };

  const handleCollaborate = useCallback((ideaTitle: string) => {
    // Show toast notification for visual feedback
    setToast({ visible: true, ideaTitle });
    
    // Hide toast after 3 seconds
    setTimeout(() => {
      setToast({ visible: false, ideaTitle: '' });
    }, 3000);
  }, []);

  // Calculate stats
  const openIdeas = ideas.filter(idea => idea.status === 'open').length;
  const inCollaboration = ideas.filter(idea => idea.status === 'in-collaboration').length;
  const totalInvestors = ideas.reduce((sum, idea) => sum + idea.investorCount, 0);

  return (
    <PageLayout
      backgroundColor="light"
      isLoggedIn={true}
      currentRole="investor"
      onLogoutClick={handleLogout}
    >
      <div className={styles.investorView}>
        <header className={styles.header}>
          <h1 className={styles.title}>Investor Dashboard</h1>
          <p className={styles.subtitle}>
            Discover innovative ideas and add them to your collaboration space
          </p>
        </header>

        {/* Stats Summary */}
        <div className={styles.statsSummary} aria-label="Ideas statistics">
          <div className={styles.statItem}>
            <span className={styles.statValue}>{ideas.length}</span>
            <span className={styles.statLabel}>Total Ideas</span>
          </div>
          <div className={styles.statItem}>
            <span className={styles.statValue}>{openIdeas}</span>
            <span className={styles.statLabel}>Open Ideas</span>
          </div>
          <div className={styles.statItem}>
            <span className={styles.statValue}>{inCollaboration}</span>
            <span className={styles.statLabel}>In Collaboration</span>
          </div>
          <div className={styles.statItem}>
            <span className={styles.statValue}>{totalInvestors}</span>
            <span className={styles.statLabel}>Total Investors</span>
          </div>
        </div>

        {/* Ideas Section */}
        <section className={styles.ideasSection} aria-label="Available Ideas">
          <h2 className={styles.sectionTitle}>Available Ideas</h2>
          
          {ideas.length > 0 ? (
            <div className={styles.ideasGrid}>
              {ideas.map(idea => (
                <IdeaCard
                  key={idea.id}
                  idea={idea}
                  variant="investor"
                  onCollaborate={() => handleCollaborate(idea.title)}
                />
              ))}
            </div>
          ) : (
            <div className={styles.emptyState}>
              <div className={styles.emptyIcon} aria-hidden="true">💡</div>
              <p className={styles.emptyText}>No ideas available at the moment</p>
            </div>
          )}
        </section>

        {/* Toast Notification for Visual Feedback */}
        {toast.visible && (
          <div 
            className={styles.toast} 
            role="status" 
            aria-live="polite"
            aria-label={`${toast.ideaTitle} added to collaboration`}
          >
            <span className={styles.toastIcon} aria-hidden="true">✓</span>
            <span className={styles.toastMessage}>
              "{toast.ideaTitle}" added to Collaboration Space
            </span>
          </div>
        )}
      </div>
    </PageLayout>
  );
}
