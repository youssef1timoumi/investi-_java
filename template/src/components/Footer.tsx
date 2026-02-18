import { Link } from 'react-router-dom';
import styles from './Footer.module.css';

interface NavigationLink {
  label: string;
  href: string;
}

interface FooterProps {
  navigationLinks?: NavigationLink[];
}

const defaultLinks: NavigationLink[] = [
  { label: 'Forum', href: '/forum' },
  { label: 'Events', href: '/events' },
  { label: 'Collaboration', href: '/collaboration' },
  { label: 'Projects', href: '/projects' },
  { label: 'Achievements', href: '/achievements' },
];

export default function Footer({ navigationLinks = defaultLinks }: FooterProps) {
  const currentYear = new Date().getFullYear();

  return (
    <footer className={styles.footer}>
      <div className={styles.container}>
        <div className={styles.brand}>
          <img
            src="/INVESTI.png"
            alt="INVESTI Logo"
            className={styles.logo}
          />
          <p className={styles.tagline}>
            Connecting innovators with investors
          </p>
        </div>

        <nav className={styles.nav} aria-label="Footer navigation">
          <h3 className={styles.navTitle}>Quick Links</h3>
          <ul className={styles.navList}>
            {navigationLinks.map((link) => (
              <li key={link.href}>
                <Link to={link.href} className={styles.navLink}>
                  {link.label}
                </Link>
              </li>
            ))}
          </ul>
        </nav>

        <div className={styles.contact}>
          <h3 className={styles.navTitle}>Contact</h3>
          <p className={styles.contactText}>info@investi.com</p>
          <p className={styles.contactText}>+1 (555) 123-4567</p>
        </div>
      </div>

      <div className={styles.bottom}>
        <p className={styles.copyright}>
          © {currentYear} INVESTI. All rights reserved.
        </p>
      </div>
    </footer>
  );
}
