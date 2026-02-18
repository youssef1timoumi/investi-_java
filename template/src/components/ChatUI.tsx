import { useEffect, useRef } from 'react';
import type { ChatMessage, UserRole } from '../types';
import styles from './ChatUI.module.css';

interface ChatUIProps {
  ideaId: string;
  messages: ChatMessage[];
  isOpen: boolean;
  onClose: () => void;
}

function formatTimestamp(timestamp: string): string {
  const date = new Date(timestamp);
  return date.toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
    hour12: true,
  });
}

function getRoleClassName(role: UserRole): string {
  switch (role) {
    case 'investor':
      return styles.roleInvestor;
    case 'innovator':
      return styles.roleInnovator;
    case 'admin':
      return styles.roleAdmin;
    default:
      return '';
  }
}

function getMessageClassName(role: UserRole): string {
  switch (role) {
    case 'investor':
      return styles.messageInvestor;
    case 'innovator':
      return styles.messageInnovator;
    case 'admin':
      return styles.messageAdmin;
    default:
      return '';
  }
}

export default function ChatUI({ ideaId, messages, isOpen, onClose }: ChatUIProps) {
  const panelRef = useRef<HTMLDivElement>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const previousActiveElement = useRef<HTMLElement | null>(null);

  // ideaId is used to associate the chat with a specific idea
  const chatTitle = `Idea Discussion #${ideaId}`;

  // Focus management and body scroll lock
  useEffect(() => {
    if (isOpen) {
      previousActiveElement.current = document.activeElement as HTMLElement;
      panelRef.current?.focus();
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
      previousActiveElement.current?.focus();
    }

    return () => {
      document.body.style.overflow = '';
    };
  }, [isOpen]);

  // Scroll to bottom when messages change
  useEffect(() => {
    if (isOpen && messagesEndRef.current && messagesEndRef.current.scrollIntoView) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [messages, isOpen]);

  // Handle escape key
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div 
      className={styles.overlay} 
      onClick={onClose} 
      role="presentation"
      data-testid="chat-overlay"
    >
      <div
        ref={panelRef}
        className={styles.panel}
        role="dialog"
        aria-modal="true"
        aria-labelledby="chat-title"
        tabIndex={-1}
        onClick={(e) => e.stopPropagation()}
        data-testid="chat-panel"
      >
        <header className={styles.header}>
          <h2 id="chat-title" className={styles.title}>
            {chatTitle}
          </h2>
          <button
            className={styles.closeButton}
            onClick={onClose}
            aria-label="Close chat"
            data-testid="chat-close-button"
          >
            ✕
          </button>
        </header>

        <div 
          className={styles.messagesContainer}
          role="log"
          aria-live="polite"
          aria-label="Chat messages"
          data-testid="chat-messages"
        >
          {messages.length === 0 ? (
            <div className={styles.emptyState}>
              <span className={styles.emptyIcon} aria-hidden="true">💬</span>
              <p>No messages yet for this idea.</p>
              <p>Start the conversation!</p>
            </div>
          ) : (
            messages.map((message) => (
              <article
                key={message.id}
                className={`${styles.message} ${getMessageClassName(message.senderRole)}`}
                data-testid={`chat-message-${message.id}`}
              >
                <div className={styles.senderInfo}>
                  <span className={styles.senderName}>{message.senderName}</span>
                  <span className={`${styles.senderRole} ${getRoleClassName(message.senderRole)}`}>
                    {message.senderRole}
                  </span>
                </div>
                <div className={styles.messageBubble}>
                  {message.content}
                </div>
                <time className={styles.timestamp} dateTime={message.timestamp}>
                  {formatTimestamp(message.timestamp)}
                </time>
              </article>
            ))
          )}
          <div ref={messagesEndRef} />
        </div>

        <div className={styles.inputContainer}>
          <div className={styles.inputWrapper}>
            <input
              type="text"
              className={styles.input}
              placeholder="Type a message..."
              aria-label="Message input (visual only)"
              disabled
              data-testid="chat-input"
            />
            <button
              className={styles.sendButton}
              aria-label="Send message (visual only)"
              disabled
              data-testid="chat-send-button"
            >
              ➤
            </button>
          </div>
          <p className={styles.visualOnlyNote}>
            This is a visual-only chat interface
          </p>
        </div>
      </div>
    </div>
  );
}
