import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import ProposalCard from '../ProposalCard';
import type { Proposal } from '../../types';

const mockProposal: Proposal = {
  id: '1',
  ideaId: '2',
  title: 'Urban Farming Network',
  description: 'Seeking partners for pilot program',
  authorId: '5',
  authorName: 'Eva Innovator',
  investorInterest: 75,
  status: 'active',
};

describe('ProposalCard', () => {
  it('renders proposal title and description', () => {
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={mockProposal} investorCount={5} onChatClick={onChatClick} />);

    expect(screen.getByText('Urban Farming Network')).toBeInTheDocument();
    expect(screen.getByText('Seeking partners for pilot program')).toBeInTheDocument();
  });

  it('renders author name', () => {
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={mockProposal} investorCount={5} onChatClick={onChatClick} />);

    expect(screen.getByText('By Eva Innovator')).toBeInTheDocument();
  });

  it('renders investor count', () => {
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={mockProposal} investorCount={5} onChatClick={onChatClick} />);

    expect(screen.getByText('5 investors interested')).toBeInTheDocument();
  });

  it('renders singular investor when count is 1', () => {
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={mockProposal} investorCount={1} onChatClick={onChatClick} />);

    expect(screen.getByText('1 investor interested')).toBeInTheDocument();
  });

  it('renders status badge for active status', () => {
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={mockProposal} investorCount={5} onChatClick={onChatClick} />);

    expect(screen.getByText('Active')).toBeInTheDocument();
  });

  it('renders status badge for pending status', () => {
    const pendingProposal: Proposal = { ...mockProposal, status: 'pending' };
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={pendingProposal} investorCount={3} onChatClick={onChatClick} />);

    expect(screen.getByText('Pending')).toBeInTheDocument();
  });

  it('renders status badge for converted status', () => {
    const convertedProposal: Proposal = { ...mockProposal, status: 'converted' };
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={convertedProposal} investorCount={10} onChatClick={onChatClick} />);

    expect(screen.getByText('Converted')).toBeInTheDocument();
  });

  // Investor Interest Indicator Tests (Requirement 8.2)
  it('renders investor interest meter with correct value', () => {
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={mockProposal} investorCount={5} onChatClick={onChatClick} />);

    expect(screen.getByText('Investor Interest')).toBeInTheDocument();
    expect(screen.getByText('75%')).toBeInTheDocument();
  });

  it('renders investor interest meter with proper accessibility attributes', () => {
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={mockProposal} investorCount={5} onChatClick={onChatClick} />);

    const meter = screen.getByRole('meter');
    expect(meter).toHaveAttribute('aria-valuenow', '75');
    expect(meter).toHaveAttribute('aria-valuemin', '0');
    expect(meter).toHaveAttribute('aria-valuemax', '100');
    expect(meter).toHaveAttribute('aria-label', 'Investor interest: 75%');
  });

  it('shows high interest badge for interest >= 70%', () => {
    const highInterestProposal: Proposal = { ...mockProposal, investorInterest: 85 };
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={highInterestProposal} investorCount={5} onChatClick={onChatClick} />);

    expect(screen.getByText('🔥 High Interest')).toBeInTheDocument();
  });

  it('shows growing interest badge for interest between 40-69%', () => {
    const mediumInterestProposal: Proposal = { ...mockProposal, investorInterest: 55 };
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={mediumInterestProposal} investorCount={5} onChatClick={onChatClick} />);

    expect(screen.getByText('📈 Growing Interest')).toBeInTheDocument();
  });

  it('shows early stage badge for interest < 40%', () => {
    const lowInterestProposal: Proposal = { ...mockProposal, investorInterest: 25 };
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={lowInterestProposal} investorCount={2} onChatClick={onChatClick} />);

    expect(screen.getByText('🌱 Early Stage')).toBeInTheDocument();
  });

  // Chat Entry Point Button Tests (Requirement 8.3)
  it('renders chat entry point button', () => {
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={mockProposal} investorCount={5} onChatClick={onChatClick} />);

    const chatButton = screen.getByRole('button', { name: /start chat about urban farming network/i });
    expect(chatButton).toBeInTheDocument();
  });

  it('calls onChatClick when chat button is clicked', () => {
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={mockProposal} investorCount={5} onChatClick={onChatClick} />);

    const chatButton = screen.getByRole('button', { name: /start chat/i });
    fireEvent.click(chatButton);

    expect(onChatClick).toHaveBeenCalledTimes(1);
  });

  it('displays chat button text with icon', () => {
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={mockProposal} investorCount={5} onChatClick={onChatClick} />);

    expect(screen.getByText('Start Chat')).toBeInTheDocument();
    expect(screen.getByText('💬')).toBeInTheDocument();
  });

  // Edge cases
  it('handles zero investor interest', () => {
    const zeroInterestProposal: Proposal = { ...mockProposal, investorInterest: 0 };
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={zeroInterestProposal} investorCount={0} onChatClick={onChatClick} />);

    expect(screen.getByText('0%')).toBeInTheDocument();
    expect(screen.getByText('🌱 Early Stage')).toBeInTheDocument();
  });

  it('handles 100% investor interest', () => {
    const fullInterestProposal: Proposal = { ...mockProposal, investorInterest: 100 };
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={fullInterestProposal} investorCount={20} onChatClick={onChatClick} />);

    expect(screen.getByText('100%')).toBeInTheDocument();
    expect(screen.getByText('🔥 High Interest')).toBeInTheDocument();
  });

  it('handles boundary value of 40% interest (medium threshold)', () => {
    const boundaryProposal: Proposal = { ...mockProposal, investorInterest: 40 };
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={boundaryProposal} investorCount={3} onChatClick={onChatClick} />);

    expect(screen.getByText('📈 Growing Interest')).toBeInTheDocument();
  });

  it('handles boundary value of 70% interest (high threshold)', () => {
    const boundaryProposal: Proposal = { ...mockProposal, investorInterest: 70 };
    const onChatClick = vi.fn();
    render(<ProposalCard proposal={boundaryProposal} investorCount={8} onChatClick={onChatClick} />);

    expect(screen.getByText('🔥 High Interest')).toBeInTheDocument();
  });
});
