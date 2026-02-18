import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import CourseCard from '../CourseCard';
import type { Course } from '../../types';

const mockCourse: Course = {
  id: '1',
  title: 'Pitching 101',
  description: 'Master the art of the perfect pitch',
  duration: '4 weeks',
  instructor: 'Sarah Chen',
  imageUrl: '/course1.jpg',
  enrolledCount: 234,
};

describe('CourseCard', () => {
  it('renders course title, description, duration, and instructor', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={false} />);

    expect(screen.getByText('Pitching 101')).toBeInTheDocument();
    expect(screen.getByText('Master the art of the perfect pitch')).toBeInTheDocument();
    expect(screen.getByText('4 weeks')).toBeInTheDocument();
    expect(screen.getByText('Instructor: Sarah Chen')).toBeInTheDocument();
  });

  it('renders course image with correct alt text', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={false} />);

    const image = screen.getByAltText('Pitching 101 course');
    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('src', '/course1.jpg');
  });

  it('displays enroll button when not enrolled', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={false} />);

    const enrollButton = screen.getByRole('button', { name: /enroll in pitching 101/i });
    expect(enrollButton).toBeInTheDocument();
    expect(enrollButton).toHaveTextContent('Enroll Now');
    expect(enrollButton).not.toBeDisabled();
  });

  it('calls onEnroll when enroll button is clicked', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={false} />);

    const enrollButton = screen.getByRole('button', { name: /enroll in pitching 101/i });
    fireEvent.click(enrollButton);

    expect(onEnroll).toHaveBeenCalledTimes(1);
  });

  it('provides visual feedback after enrollment', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={false} />);

    const enrollButton = screen.getByRole('button', { name: /enroll in pitching 101/i });
    fireEvent.click(enrollButton);

    // Check for enrollment feedback
    expect(screen.getByText("You're enrolled in this course!")).toBeInTheDocument();
    // Check that "Enrolled" appears in multiple places (badge and button)
    expect(screen.getAllByText('Enrolled')).toHaveLength(2);
  });

  it('disables button after enrollment', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={false} />);

    const enrollButton = screen.getByRole('button', { name: /enroll in pitching 101/i });
    fireEvent.click(enrollButton);

    // Button should now be disabled
    const enrolledButton = screen.getByRole('button', { name: /already enrolled in pitching 101/i });
    expect(enrolledButton).toBeDisabled();
  });

  it('renders as enrolled when isEnrolled prop is true', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={true} />);

    const enrolledButton = screen.getByRole('button', { name: /already enrolled in pitching 101/i });
    expect(enrolledButton).toBeInTheDocument();
    expect(enrolledButton).toBeDisabled();
    expect(enrolledButton).toHaveTextContent('Enrolled');
  });

  it('shows enrolled count when not enrolled', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={false} />);

    expect(screen.getByText('234 students enrolled')).toBeInTheDocument();
  });

  it('shows updated enrolled count when enrolled', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={true} />);

    expect(screen.getByText('235 students enrolled')).toBeInTheDocument();
  });

  it('shows "Enrolled" badge when enrolled', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={true} />);

    expect(screen.getByLabelText('Enrolled in course')).toBeInTheDocument();
  });

  it('has proper accessibility attributes', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={false} />);

    // Check button has aria-label
    const enrollButton = screen.getByRole('button', { name: /enroll in pitching 101/i });
    expect(enrollButton).toHaveAttribute('aria-label');

    // Check image has alt text
    const image = screen.getByAltText('Pitching 101 course');
    expect(image).toBeInTheDocument();
  });

  it('does not call onEnroll when already enrolled', () => {
    const onEnroll = vi.fn();
    render(<CourseCard course={mockCourse} onEnroll={onEnroll} isEnrolled={true} />);

    const enrolledButton = screen.getByRole('button', { name: /already enrolled in pitching 101/i });
    fireEvent.click(enrolledButton);

    expect(onEnroll).not.toHaveBeenCalled();
  });
});
