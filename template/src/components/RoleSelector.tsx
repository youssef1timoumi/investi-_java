import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './RoleSelector.module.css';

interface RoleSelectorProps {
  onRoleSelect: (role: 'admin' | 'investor' | 'innovator') => void;
  preselectedRole?: 'admin' | 'investor' | 'innovator' | null;
}

interface RoleOption {
  id: 'admin' | 'investor' | 'innovator';
  title: string;
  description: string;
  icon: React.ReactNode;
  route: string;
}

const roleOptions: RoleOption[] = [
  {
    id: 'admin',
    title: 'Admin',
    description: 'Manage platform settings, users, and view analytics dashboard',
    icon: (
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
      >
        <path d="M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z" />
        <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1Z" />
      </svg>
    ),
    route: '/admin',
  },
  {
    id: 'investor',
    title: 'Investor',
    description: 'Browse ideas and add promising innovations to collaboration',
    icon: (
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
      >
        <line x1="12" y1="1" x2="12" y2="23" />
        <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
      </svg>
    ),
    route: '/investor',
  },
  {
    id: 'innovator',
    title: 'Innovator',
    description: 'View ideas, invest in innovations, and collaborate with others',
    icon: (
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
      >
        <path d="M12 2v8" />
        <path d="m4.93 10.93 1.41 1.41" />
        <path d="M2 18h2" />
        <path d="M20 18h2" />
        <path d="m19.07 10.93-1.41 1.41" />
        <path d="M22 22H2" />
        <path d="m8 22 4-10 4 10" />
      </svg>
    ),
    route: '/innovator',
  },
];

export default function RoleSelector({ onRoleSelect, preselectedRole = null }: RoleSelectorProps) {
  const [selectedRole, setSelectedRole] = useState<'admin' | 'investor' | 'innovator' | null>(null);
  const navigate = useNavigate();

  // Handle preselected role from URL query parameter
  useEffect(() => {
    if (preselectedRole && !selectedRole) {
      const role = roleOptions.find(r => r.id === preselectedRole);
      if (role) {
        setSelectedRole(role.id);
        onRoleSelect(role.id);
        // Navigate after a brief delay to show selection feedback
        setTimeout(() => {
          navigate(role.route);
        }, 300);
      }
    }
  }, [preselectedRole, selectedRole, navigate, onRoleSelect]);

  const handleRoleClick = (role: RoleOption) => {
    setSelectedRole(role.id);
    onRoleSelect(role.id);
    // Navigate after a brief delay to show selection feedback
    setTimeout(() => {
      navigate(role.route);
    }, 200);
  };

  const handleKeyDown = (event: React.KeyboardEvent, role: RoleOption) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      handleRoleClick(role);
    }
  };

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>Select Your Role</h2>
      <p className={styles.subtitle}>Choose how you want to access the platform</p>
      <div className={styles.roleGrid} role="group" aria-label="Role selection">
        {roleOptions.map((role) => (
          <div
            key={role.id}
            className={`${styles.roleCard} ${selectedRole === role.id ? styles.selected : ''}`}
            onClick={() => handleRoleClick(role)}
            onKeyDown={(e) => handleKeyDown(e, role)}
            role="button"
            tabIndex={0}
            aria-pressed={selectedRole === role.id}
            aria-label={`Select ${role.title} role: ${role.description}`}
          >
            <div className={styles.iconWrapper}>{role.icon}</div>
            <h3 className={styles.roleTitle}>{role.title}</h3>
            <p className={styles.roleDescription}>{role.description}</p>
            <div className={styles.selectIndicator}>
              {selectedRole === role.id ? 'Selected' : 'Click to select'}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
