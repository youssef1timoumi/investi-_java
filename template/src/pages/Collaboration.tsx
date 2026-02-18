import { useState } from 'react';
import PageLayout from '../components/PageLayout';
import { getProposals, getChatMessages, ideas } from '../data/mockData';
import ProposalCard from '../components/ProposalCard';
import ChatUI from '../components/ChatUI';
import styles from './Collaboration.module.css';

/**
 * Collaboration Page
 * 
 * Displays idea proposals and investor interest, allowing users to
 * view collaboration opportunities and start chats about ideas.
 * 
 * Requirements: 8.1, 8.2, 8.3, 8.4
 */
export default function Collaboration() {
  const proposals = getProposals();
  const [chatState, setChatState] = useState<{
    isOpen: boolean;
    ideaId: string | null;
  }>({
    isOpen: false,
    ideaId: null,
  });

  // Calculate stats
  const activeProposals = proposals.filter(p => p.status === 'active').length;
  const pendingProposals = proposals.filter(p => p.status === 'pending').length;
  const convertedProposals = proposals.filter(p => p.status === 'converted').length;

  const handleChatClick = (ideaId: string) => {
    setChatState({
      isOpen: true,
      ideaId,
    });
  };

  const handleCloseChat = () => {
    setChatState({
      isOpen: false,
      ideaId: null,
    });
  };

  // Get investor count for a proposal based on the related idea
  const getInvestorCount = (ideaId: string): number => {
    const idea = ideas.find(i => i.id === ideaId);
    return idea?.investorCount || 0;
  };

  return (
    <PageLayout backgroundColor="light">
      <div className={styles.collaborationPage}>
        <div className={styles.container}>
          {/* Header Section */}
          <header className={styles.headerSection}>
            <h1 className={styles.pageTitle}>Collaboration Space</h1>
            <p className={styles.pageDescription}>
              Explore idea proposals and connect with innovators. 
              View investor interest and start conversations about promising opportunities.
            </p>
          </header>

          {/* Stats Summary */}
          <section 
            className={styles.statsSummary}
            aria-label="Proposal statistics"
          >
            <div className={styles.statItem}>
              <span className={styles.statValue}>{proposals.length}</span>
              <span className={styles.statLabel}>Total Proposals</span>
            </div>
            <div className={styles.statItem}>
              <span className={styles.statValue}>{activeProposals}</span>
              <span className={styles.statLabel}>Active</span>
            </div>
            <div className={styles.statItem}>
              <span className={styles.statValue}>{pendingProposals}</span>
              <span className={styles.statLabel}>Pending</span>
            </div>
            <div className={styles.statItem}>
              <span className={styles.statValue}>{convertedProposals}</span>
              <span className={styles.statLabel}>Converted</span>
            </div>
          </section>

          {/* Proposals Grid */}
          <section aria-label="Idea proposals">
            {proposals.length > 0 ? (
              <div className={styles.proposalsGrid}>
                {proposals.map((proposal) => (
                  <ProposalCard
                    key={proposal.id}
                    proposal={proposal}
                    investorCount={getInvestorCount(proposal.ideaId)}
                    onChatClick={() => handleChatClick(proposal.ideaId)}
                  />
                ))}
              </div>
            ) : (
              <div className={styles.emptyState} role="status">
                <span className={styles.emptyIcon} aria-hidden="true">🤝</span>
                <h2 className={styles.emptyTitle}>No Proposals Yet</h2>
                <p className={styles.emptyDescription}>
                  Collaboration proposals will appear here once innovators submit their ideas for partnership.
                </p>
              </div>
            )}
          </section>
        </div>

        {/* Chat UI Modal */}
        {chatState.ideaId && (
          <ChatUI
            ideaId={chatState.ideaId}
            messages={getChatMessages(chatState.ideaId)}
            isOpen={chatState.isOpen}
            onClose={handleCloseChat}
          />
        )}
      </div>
    </PageLayout>
  );
}
