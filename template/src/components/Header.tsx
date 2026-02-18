import { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import styles from './Header.module.css';

interface HeaderProps {
  isLoggedIn?: boolean;
  currentRole?: 'admin' | 'investor' | 'innovator';
  onLoginClick?: () => void;
  onLogoutClick?: () => void;
}

export default function Header({
  isLoggedIn = false,
  onLoginClick,
  onLogoutClick,
}: HeaderProps) {
  const [menuOpen, setMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 50);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleLoginClick = () => {
    if (onLoginClick) {
      onLoginClick();
    } else {
      navigate('/login');
    }
  };

  const handleLogoutClick = () => {
    if (onLogoutClick) {
      onLogoutClick();
    }
  };

  const navLinks = [
    { label: 'Home', href: '/' },
    { label: 'Forum', href: '/forum' },
    { label: 'Events', href: '/events' },
    { label: 'Projects', href: '/projects' },
    { label: 'Achievements', href: '/achievements' },
  ];

  return (
    <header className={`${styles.header} ${scrolled ? styles.scrolled : ''}`}>
      <div className={styles.container}>
        <Link to="/" className={styles.logoLink} aria-label="Go to homepage">
          <img
            src="/INVESTI.png"
            alt="INVESTI Logo"
            className={styles.logo}
          />
        </Link>

        <button
          className={styles.hamburger}
          onClick={() => setMenuOpen(!menuOpen)}
          aria-expanded={menuOpen}
          aria-label="Toggle navigation menu"
        >
          <span className={styles.hamburgerLine} />
          <span className={styles.hamburgerLine} />
          <span className={styles.hamburgerLine} />
        </button>

        <nav 
          className={`${styles.nav} ${menuOpen ? styles.navOpen : ''}`}
          aria-label="Main navigation"
        >
          <ul className={styles.navList} role="list">
            {navLinks.map((link) => {
              const isActive = location.pathname === link.href;
              return (
                <li key={link.href}>
                  <Link
                    to={link.href}
                    className={`${styles.navLink} ${isActive ? styles.navLinkActive : ''}`}
                    onClick={() => setMenuOpen(false)}
                    aria-current={isActive ? 'page' : undefined}
                  >
                    {link.label}
                  </Link>
                </li>
              );
            })}
          </ul>

          <div className={styles.authButtons}>
            {isLoggedIn ? (
              <button 
                className={styles.signInButton} 
                onClick={handleLogoutClick}
              >
                Logout
              </button>
            ) : (
              <>
                <button 
                  className={styles.signInButton} 
                  onClick={handleLoginClick}
                >
                  Sign In
                </button>
                <button 
                  className={styles.startButton} 
                  onClick={() => navigate('/login')}
                >
                  Start Free <span className={styles.arrow}>→</span>
                </button>
              </>
            )}
          </div>
        </nav>
      </div>
    </header>
  );
}
