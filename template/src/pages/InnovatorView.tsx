import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import PageLayout from '../components/PageLayout';
import IdeaCard from '../components/IdeaCard';
import ChatUI from '../components/ChatUI';
import { getIdeas, getChatMessages } from '../data/mockData';
import type { Idea } from '../types';
import styles from './InnovatorView.module.css';

interface ChatState {
  isOpen: boolean;
  activeIdea: Idea | null;
}

export default function InnovatorView() {
  const navigate = useNavigate();
  const ideas = getIdeas();
  const [chatState, setChatState] = useState<ChatState>({
    isOpen: false,
    activeIdea: null,
  });

  const handleLogout = () => {
    navigate('/');
  };

  const handleInvest = useCallback((idea: Idea) => {
    // Open ChatUI with the selected idea
    setChatState({
      isOpen: true,
      activeIdea: idea,
    });
  }, []);

  const handleCloseChat = useCallback(() => {
    setChatState({
      isOpen: false,
      activeIdea: null,
    });
  }, []);

  // Get chat messages for the active idea
  const chatMessages = chatState.activeIdea
    ? getChatMessages(chatState.activeIdea.id)
    : [];

  // Calculate stats
  const openIdeas = ideas.filter(idea => idea.status === 'open').length;
  const fundedIdeas = ideas.filter(idea => idea.status === 'funded').length;
  const totalInvestors = ideas.reduce((sum, idea) => sum + idea.investorCount, 0);

  return (
    <PageLayout
      backgroundColor="light"
      isLoggedIn={true}
      currentRole="innovator"
      onLogoutClick={handleLogout}
    >
      <div className={styles.innovatorView}>
        <header className={styles.header}>
          <h1 className={styles.title}>Innovator Dashboard</h1>
          <p className={styles.subtitle}>
            Explore innovative ideas and invest in promising ventures
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
            <span className={styles.statValue}>{fundedIdeas}</span>
            <span className={styles.statLabel}>Funded Ideas</span>
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
                  variant="innovator"
                  onInvest={() => handleInvest(idea)}
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

        {/* ChatUI Modal */}
        <ChatUI
          ideaId={chatState.activeIdea?.id || ''}
          messages={chatMessages}
          isOpen={chatState.isOpen}
          onClose={handleCloseChat}
        />
      </div>
    </PageLayout>
  );
}
