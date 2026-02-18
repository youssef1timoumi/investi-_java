import type { ReactNode } from 'react';
import Header from './Header';
import Footer from './Footer';
import styles from './PageLayout.module.css';

interface PageLayoutProps {
  children: ReactNode;
  backgroundColor?: 'dark' | 'light';
  isLoggedIn?: boolean;
  currentRole?: 'admin' | 'investor' | 'innovator';
  onLoginClick?: () => void;
  onLogoutClick?: () => void;
}

export default function PageLayout({
  children,
  backgroundColor = 'light',
  isLoggedIn = false,
  currentRole,
  onLoginClick,
  onLogoutClick,
}: PageLayoutProps) {
  return (
    <div className={`${styles.layout} ${styles[backgroundColor]}`}>
      <a href="#main-content" className={styles.skipLink}>
        Skip to main content
      </a>
      <Header
        isLoggedIn={isLoggedIn}
        currentRole={currentRole}
        onLoginClick={onLoginClick}
        onLogoutClick={onLogoutClick}
      />
      <main id="main-content" className={styles.main} tabIndex={-1}>
        {children}
      </main>
      <Footer />
    </div>
  );
}
