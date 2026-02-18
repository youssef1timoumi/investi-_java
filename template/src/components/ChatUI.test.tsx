import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import ChatUI from './ChatUI';
import type { ChatMessage } from '../types';

const mockMessages: ChatMessage[] = [
  {
    id: '1',
    ideaId: '1',
    senderId: '2',
    senderName: 'Bob Investor',
    senderRole: 'investor',
    content: 'I love this concept! How far along is the prototype?',
    timestamp: '2024-01-15T10:30:00',
  },
  {
    id: '2',
    ideaId: '1',
    senderId: '3',
    senderName: 'Carol Innovator',
    senderRole: 'innovator',
    content: 'We have a working MVP with 85% accuracy on sorting.',
    timestamp: '2024-01-15T10:35:00',
  },
  {
    id: '3',
    ideaId: '1',
    senderId: '1',
    senderName: 'Alice Admin',
    senderRole: 'admin',
    content: 'Great progress! Keep us updated.',
    timestamp: '2024-01-15T10:40:00',
  },
];

describe('ChatUI', () => {
  it('renders nothing when isOpen is false', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="1"
        messages={mockMessages}
        isOpen={false}
        onClose={onClose}
      />
    );

    expect(screen.queryByTestId('chat-panel')).not.toBeInTheDocument();
  });

  it('renders the chat panel when isOpen is true', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="1"
        messages={mockMessages}
        isOpen={true}
        onClose={onClose}
      />
    );

    expect(screen.getByTestId('chat-panel')).toBeInTheDocument();
    expect(screen.getByText('Idea Discussion #1')).toBeInTheDocument();
  });

  it('displays all messages with sender info and timestamps', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="1"
        messages={mockMessages}
        isOpen={true}
        onClose={onClose}
      />
    );

    // Check all messages are displayed
    expect(screen.getByText('I love this concept! How far along is the prototype?')).toBeInTheDocument();
    expect(screen.getByText('We have a working MVP with 85% accuracy on sorting.')).toBeInTheDocument();
    expect(screen.getByText('Great progress! Keep us updated.')).toBeInTheDocument();

    // Check sender names are displayed
    expect(screen.getByText('Bob Investor')).toBeInTheDocument();
    expect(screen.getByText('Carol Innovator')).toBeInTheDocument();
    expect(screen.getByText('Alice Admin')).toBeInTheDocument();

    // Check roles are displayed
    expect(screen.getByText('investor')).toBeInTheDocument();
    expect(screen.getByText('innovator')).toBeInTheDocument();
    expect(screen.getByText('admin')).toBeInTheDocument();
  });

  it('displays empty state when no messages', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="1"
        messages={[]}
        isOpen={true}
        onClose={onClose}
      />
    );

    expect(screen.getByText('No messages yet for this idea.')).toBeInTheDocument();
    expect(screen.getByText('Start the conversation!')).toBeInTheDocument();
  });

  it('has a visual-only input field that is disabled', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="1"
        messages={mockMessages}
        isOpen={true}
        onClose={onClose}
      />
    );

    const input = screen.getByTestId('chat-input');
    expect(input).toBeDisabled();
    expect(input).toHaveAttribute('placeholder', 'Type a message...');
  });

  it('has a visual-only send button that is disabled', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="1"
        messages={mockMessages}
        isOpen={true}
        onClose={onClose}
      />
    );

    const sendButton = screen.getByTestId('chat-send-button');
    expect(sendButton).toBeDisabled();
  });

  it('displays visual-only note', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="1"
        messages={mockMessages}
        isOpen={true}
        onClose={onClose}
      />
    );

    expect(screen.getByText('This is a visual-only chat interface')).toBeInTheDocument();
  });

  it('calls onClose when close button is clicked', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="1"
        messages={mockMessages}
        isOpen={true}
        onClose={onClose}
      />
    );

    const closeButton = screen.getByTestId('chat-close-button');
    fireEvent.click(closeButton);

    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('calls onClose when overlay is clicked', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="1"
        messages={mockMessages}
        isOpen={true}
        onClose={onClose}
      />
    );

    const overlay = screen.getByTestId('chat-overlay');
    fireEvent.click(overlay);

    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('does not call onClose when panel is clicked', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="1"
        messages={mockMessages}
        isOpen={true}
        onClose={onClose}
      />
    );

    const panel = screen.getByTestId('chat-panel');
    fireEvent.click(panel);

    expect(onClose).not.toHaveBeenCalled();
  });

  it('has proper accessibility attributes', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="1"
        messages={mockMessages}
        isOpen={true}
        onClose={onClose}
      />
    );

    const panel = screen.getByTestId('chat-panel');
    expect(panel).toHaveAttribute('role', 'dialog');
    expect(panel).toHaveAttribute('aria-modal', 'true');
    expect(panel).toHaveAttribute('aria-labelledby', 'chat-title');

    const messagesContainer = screen.getByTestId('chat-messages');
    expect(messagesContainer).toHaveAttribute('role', 'log');
    expect(messagesContainer).toHaveAttribute('aria-live', 'polite');

    const closeButton = screen.getByTestId('chat-close-button');
    expect(closeButton).toHaveAttribute('aria-label', 'Close chat');
  });

  it('displays idea-specific title', () => {
    const onClose = vi.fn();
    render(
      <ChatUI
        ideaId="42"
        messages={[]}
        isOpen={true}
        onClose={onClose}
      />
    );

    expect(screen.getByText('Idea Discussion #42')).toBeInTheDocument();
  });
});
