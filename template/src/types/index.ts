// User and Role Types
export type UserRole = 'admin' | 'investor' | 'innovator';

export interface User {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  avatar?: string;
  points: number;
  badges: string[];
}

// Idea Types
export interface Idea {
  id: string;
  title: string;
  description: string;
  authorId: string;
  authorName: string;
  status: 'open' | 'in-collaboration' | 'funded' | 'project';
  investorCount: number;
  createdAt: string;
  tags: string[];
}

// Forum Types
export interface Post {
  id: string;
  title: string;
  content: string;
  authorId: string;
  authorName: string;
  authorAvatar?: string;
  upvotes: number;
  downvotes: number;
  commentCount: number;
  createdAt: string;
  category?: string;
  isTrending?: boolean;
}

export interface Comment {
  id: string;
  postId: string;
  parentId?: string;
  content: string;
  authorId: string;
  authorName: string;
  upvotes: number;
  downvotes: number;
  createdAt: string;
  replies?: Comment[];
}

// Event Types
export interface Event {
  id: string;
  title: string;
  description: string;
  date: string;
  location: string;
  imageUrl: string;
  capacity: number;
  inscribedCount: number;
}

export interface Course {
  id: string;
  title: string;
  description: string;
  duration: string;
  instructor: string;
  imageUrl: string;
  enrolledCount: number;
}

// Collaboration Types
export interface Proposal {
  id: string;
  ideaId: string;
  title: string;
  description: string;
  authorId: string;
  authorName: string;
  investorInterest: number;
  status: 'pending' | 'active' | 'converted';
}

export interface ChatMessage {
  id: string;
  ideaId: string;
  senderId: string;
  senderName: string;
  senderRole: UserRole;
  content: string;
  timestamp: string;
}

// Project Types
export interface Project {
  id: string;
  title: string;
  description: string;
  originIdeaId: string;
  progress: number;
  status: 'planning' | 'in-progress' | 'completed';
  teamMembers: string[];
  startDate: string;
}

// Achievement Types
export interface Badge {
  id: string;
  name: string;
  description: string;
  icon: string;
  earnedAt?: string;
}

export interface Quiz {
  id: string;
  title: string;
  description: string;
  pointsReward: number;
  questionCount: number;
  isCompleted: boolean;
}

// Dashboard Metrics
export interface DashboardMetrics {
  usersCount: number;
  ideasCount: number;
  eventsCount: number;
  projectsCount: number;
  activeCollaborations: number;
}

// Navigation Types
export interface NavigationLink {
  label: string;
  href: string;
  icon?: string;
}

export interface ServiceInfo {
  id: string;
  title: string;
  description: string;
  icon: string;
  href: string;
}

// Mock Data Store Type
export interface MockDataStore {
  users: User[];
  ideas: Idea[];
  posts: Post[];
  comments: Comment[];
  events: Event[];
  courses: Course[];
  proposals: Proposal[];
  chatMessages: Record<string, ChatMessage[]>;
  projects: Project[];
  badges: Badge[];
  quizzes: Quiz[];
  dashboardMetrics: DashboardMetrics;
  services: ServiceInfo[];
}
