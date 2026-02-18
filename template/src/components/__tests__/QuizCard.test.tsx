import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import QuizCard from '../QuizCard';
import type { Quiz } from '../../types';

const mockQuiz: Quiz = {
  id: 'quiz-1',
  title: 'Investment Basics',
  description: 'Learn the fundamentals of startup investment',
  pointsReward: 50,
  questionCount: 10,
  isCompleted: false,
};

const completedQuiz: Quiz = {
  id: 'quiz-2',
  title: 'Advanced Strategies',
  description: 'Master advanced investment strategies',
  pointsReward: 100,
  questionCount: 15,
  isCompleted: true,
};

describe('QuizCard', () => {
  it('renders quiz title', () => {
    render(<QuizCard quiz={mockQuiz} />);
    
    expect(screen.getByText('Investment Basics')).toBeInTheDocument();
  });

  it('renders quiz description', () => {
    render(<QuizCard quiz={mockQuiz} />);
    
    expect(screen.getByText('Learn the fundamentals of startup investment')).toBeInTheDocument();
  });

  it('displays point value prominently', () => {
    render(<QuizCard quiz={mockQuiz} />);
    
    expect(screen.getByText('50 pts')).toBeInTheDocument();
  });

  it('displays question count', () => {
    render(<QuizCard quiz={mockQuiz} />);
    
    expect(screen.getByText('10 questions')).toBeInTheDocument();
  });

  it('handles singular question count', () => {
    const singleQuestionQuiz: Quiz = {
      ...mockQuiz,
      questionCount: 1,
    };
    render(<QuizCard quiz={singleQuestionQuiz} />);
    
    expect(screen.getByText('1 question')).toBeInTheDocument();
  });

  it('shows Start Quiz button for incomplete quiz', () => {
    render(<QuizCard quiz={mockQuiz} />);
    
    const button = screen.getByRole('button', { name: /start investment basics quiz/i });
    expect(button).toBeInTheDocument();
    expect(button).toHaveTextContent('Start Quiz');
  });

  it('has disabled Start Quiz button (visual only)', () => {
    render(<QuizCard quiz={mockQuiz} />);
    
    const button = screen.getByRole('button', { name: /start investment basics quiz/i });
    expect(button).toBeDisabled();
  });

  it('shows completion status for completed quiz', () => {
    render(<QuizCard quiz={completedQuiz} />);
    
    expect(screen.getByRole('status', { name: 'Quiz completed' })).toBeInTheDocument();
    // "Completed" appears in both the badge and button for completed quizzes
    expect(screen.getAllByText('Completed')).toHaveLength(2);
  });

  it('does not show completion badge for incomplete quiz', () => {
    render(<QuizCard quiz={mockQuiz} />);
    
    expect(screen.queryByRole('status', { name: 'Quiz completed' })).not.toBeInTheDocument();
  });

  it('shows Completed button text for completed quiz', () => {
    render(<QuizCard quiz={completedQuiz} />);
    
    const button = screen.getByRole('button', { name: /already completed/i });
    expect(button).toHaveTextContent('Completed');
  });

  it('has proper accessibility attributes', () => {
    render(<QuizCard quiz={mockQuiz} />);
    
    // Check for article role with proper label
    expect(screen.getByRole('article', { name: /quiz: investment basics/i })).toBeInTheDocument();
    
    // Check for points aria-label
    expect(screen.getByLabelText('50 points reward')).toBeInTheDocument();
  });

  it('includes completed status in article aria-label', () => {
    render(<QuizCard quiz={completedQuiz} />);
    
    expect(screen.getByRole('article', { name: /quiz: advanced strategies - completed/i })).toBeInTheDocument();
  });

  it('renders with different point values', () => {
    const highPointQuiz: Quiz = {
      ...mockQuiz,
      pointsReward: 250,
    };
    render(<QuizCard quiz={highPointQuiz} />);
    
    expect(screen.getByText('250 pts')).toBeInTheDocument();
  });

  it('renders with different question counts', () => {
    const manyQuestionsQuiz: Quiz = {
      ...mockQuiz,
      questionCount: 25,
    };
    render(<QuizCard quiz={manyQuestionsQuiz} />);
    
    expect(screen.getByText('25 questions')).toBeInTheDocument();
  });
});
