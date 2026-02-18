import type {
  User, Idea, Post, Comment, Event, Course,
  Proposal, ChatMessage, Project, Badge, Quiz,
  DashboardMetrics, ServiceInfo, MockDataStore
} from '../types';

export const users: User[] = [
  { id: '1', name: 'Alice Admin', email: 'alice@investi.com', role: 'admin', points: 500, badges: ['pioneer', 'leader'] },
  { id: '2', name: 'Bob Investor', email: 'bob@investi.com', role: 'investor', points: 350, badges: ['first-investment'] },
  { id: '3', name: 'Carol Innovator', email: 'carol@investi.com', role: 'innovator', points: 420, badges: ['idea-creator'] },
  { id: '4', name: 'David Investor', email: 'david@investi.com', role: 'investor', points: 280, badges: [] },
  { id: '5', name: 'Eva Innovator', email: 'eva@investi.com', role: 'innovator', points: 600, badges: ['top-innovator', 'collaborator'] },
];

export const ideas: Idea[] = [
  { id: '1', title: 'AI-Powered Recycling', description: 'Smart bins that sort waste using computer vision', authorId: '3', authorName: 'Carol Innovator', status: 'open', investorCount: 3, createdAt: '2024-01-15', tags: ['AI', 'sustainability'] },
  { id: '2', title: 'Urban Farming Network', description: 'Connect urban farmers with local restaurants', authorId: '5', authorName: 'Eva Innovator', status: 'in-collaboration', investorCount: 5, createdAt: '2024-01-10', tags: ['agriculture', 'local'] },
  { id: '3', title: 'Elderly Care App', description: 'Mobile app connecting seniors with caregivers', authorId: '3', authorName: 'Carol Innovator', status: 'funded', investorCount: 8, createdAt: '2024-01-05', tags: ['healthcare', 'mobile'] },
  { id: '4', title: 'Green Energy Marketplace', description: 'Platform for trading renewable energy credits', authorId: '5', authorName: 'Eva Innovator', status: 'open', investorCount: 2, createdAt: '2024-01-20', tags: ['energy', 'marketplace'] },
  { id: '5', title: 'EdTech for Rural Areas', description: 'Offline-first education platform for remote communities', authorId: '3', authorName: 'Carol Innovator', status: 'project', investorCount: 12, createdAt: '2023-12-01', tags: ['education', 'social-impact'] },
];

export const posts: Post[] = [
  { id: '1', title: 'Best practices for pitching to investors', content: 'Here are my top tips for making a compelling pitch. First, know your audience - research the investors you are pitching to. Second, tell a story that resonates emotionally. Third, be clear about your ask and how you will use the funds.', authorId: '5', authorName: 'Eva Innovator', authorAvatar: 'https://i.pravatar.cc/150?u=eva', upvotes: 42, downvotes: 3, commentCount: 8, createdAt: '2024-01-18', category: 'Tips & Advice', isTrending: true },
  { id: '2', title: 'How I secured my first funding', content: 'My journey from idea to funded project took 6 months of hard work, countless rejections, and persistence. Here is what I learned along the way and how you can avoid my mistakes.', authorId: '3', authorName: 'Carol Innovator', authorAvatar: 'https://i.pravatar.cc/150?u=carol', upvotes: 67, downvotes: 2, commentCount: 15, createdAt: '2024-01-16', category: 'Success Stories', isTrending: true },
  { id: '3', title: 'Investor perspective: What we look for', content: 'As an investor, I evaluate ideas based on team strength, market size, unique value proposition, and scalability. Let me break down each factor and what makes a startup stand out.', authorId: '2', authorName: 'Bob Investor', authorAvatar: 'https://i.pravatar.cc/150?u=bob', upvotes: 89, downvotes: 5, commentCount: 23, createdAt: '2024-01-14', category: 'Investor Insights', isTrending: true },
  { id: '4', title: 'Collaboration tips for remote teams', content: 'Working with distributed teams requires clear communication, the right tools, and trust. Here are strategies that have worked for my team across 3 time zones.', authorId: '5', authorName: 'Eva Innovator', authorAvatar: 'https://i.pravatar.cc/150?u=eva', upvotes: 31, downvotes: 1, commentCount: 6, createdAt: '2024-01-12', category: 'Collaboration', isTrending: false },
  { id: '5', title: 'Platform updates January 2024', content: 'We have exciting new features coming including improved matching algorithms, real-time chat, and a redesigned dashboard. Stay tuned for more updates!', authorId: '1', authorName: 'Alice Admin', authorAvatar: 'https://i.pravatar.cc/150?u=alice', upvotes: 54, downvotes: 0, commentCount: 12, createdAt: '2024-01-10', category: 'Announcements', isTrending: false },
];

export const comments: Comment[] = [
  { id: '1', postId: '1', content: 'Great tips! The storytelling part really helped me.', authorId: '3', authorName: 'Carol Innovator', upvotes: 12, downvotes: 0, createdAt: '2024-01-18' },
  { id: '2', postId: '1', content: 'I would add: know your numbers inside out.', authorId: '2', authorName: 'Bob Investor', upvotes: 8, downvotes: 1, createdAt: '2024-01-18' },
  { id: '3', postId: '2', content: 'Inspiring story! How did you find your first investor?', authorId: '4', authorName: 'David Investor', upvotes: 5, downvotes: 0, createdAt: '2024-01-16' },
  { id: '4', postId: '3', content: 'This is exactly what I needed to hear.', authorId: '5', authorName: 'Eva Innovator', upvotes: 15, downvotes: 0, createdAt: '2024-01-14' },
  { id: '5', postId: '3', parentId: '4', content: 'Agreed! Very insightful perspective.', authorId: '3', authorName: 'Carol Innovator', upvotes: 3, downvotes: 0, createdAt: '2024-01-14' },
];

export const events: Event[] = [
  { id: '1', title: 'Startup Pitch Night', description: 'Present your ideas to a panel of investors and get real-time feedback on your pitch deck', date: '2024-02-15', location: 'Innovation Hub, NYC', imageUrl: 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800&q=80', capacity: 100, inscribedCount: 78 },
  { id: '2', title: 'Networking Mixer', description: 'Connect with fellow innovators and investors in a casual setting with drinks and appetizers', date: '2024-02-20', location: 'Tech Center, SF', imageUrl: 'https://images.unsplash.com/photo-1511578314322-379afb476865?w=800&q=80', capacity: 50, inscribedCount: 45 },
  { id: '3', title: 'Funding Workshop', description: 'Learn the ins and outs of securing funding from seed to Series A with expert guidance', date: '2024-02-25', location: 'Virtual Event', imageUrl: 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=800&q=80', capacity: 200, inscribedCount: 156 },
  { id: '4', title: 'Demo Day Spring 2024', description: 'Showcase your MVP to potential partners, investors, and media in our flagship event', date: '2024-03-10', location: 'Convention Center, LA', imageUrl: 'https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=800&q=80', capacity: 300, inscribedCount: 210 },
  { id: '5', title: 'Investor Q&A Session', description: 'Ask questions directly to experienced investors and learn what makes startups succeed', date: '2024-03-15', location: 'Virtual Event', imageUrl: 'https://images.unsplash.com/photo-1591115765373-5207764f72e7?w=800&q=80', capacity: 150, inscribedCount: 89 },
];

export const courses: Course[] = [
  { id: '1', title: 'Pitching 101', description: 'Master the art of the perfect pitch with proven frameworks and real examples', duration: '4 weeks', instructor: 'Sarah Chen', imageUrl: 'https://images.unsplash.com/photo-1557804506-669a67965ba0?w=800&q=80', enrolledCount: 234 },
  { id: '2', title: 'Financial Modeling', description: 'Build compelling financial projections that investors actually want to see', duration: '6 weeks', instructor: 'Michael Ross', imageUrl: 'https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=800&q=80', enrolledCount: 189 },
  { id: '3', title: 'Market Validation', description: 'Validate your idea before building with customer interviews and data analysis', duration: '3 weeks', instructor: 'Lisa Park', imageUrl: 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&q=80', enrolledCount: 312 },
  { id: '4', title: 'Legal Basics for Startups', description: 'Understand contracts, IP protection, and equity structures', duration: '2 weeks', instructor: 'James Wilson', imageUrl: 'https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=800&q=80', enrolledCount: 145 },
  { id: '5', title: 'Growth Hacking', description: 'Scale your startup efficiently with proven growth strategies', duration: '5 weeks', instructor: 'Anna Martinez', imageUrl: 'https://images.unsplash.com/photo-1533750349088-cd871a92f312?w=800&q=80', enrolledCount: 267 },
];

export const proposals: Proposal[] = [
  { id: '1', ideaId: '2', title: 'Urban Farming Network', description: 'Seeking partners for pilot program', authorId: '5', authorName: 'Eva Innovator', investorInterest: 75, status: 'active' },
  { id: '2', ideaId: '1', title: 'AI-Powered Recycling', description: 'Looking for technical co-investors', authorId: '3', authorName: 'Carol Innovator', investorInterest: 45, status: 'pending' },
  { id: '3', ideaId: '4', title: 'Green Energy Marketplace', description: 'Partnership opportunity for energy sector', authorId: '5', authorName: 'Eva Innovator', investorInterest: 30, status: 'pending' },
  { id: '4', ideaId: '3', title: 'Elderly Care App', description: 'Expansion funding round', authorId: '3', authorName: 'Carol Innovator', investorInterest: 90, status: 'converted' },
  { id: '5', ideaId: '5', title: 'EdTech for Rural Areas', description: 'Impact investment opportunity', authorId: '3', authorName: 'Carol Innovator', investorInterest: 85, status: 'active' },
];


export const chatMessages: Record<string, ChatMessage[]> = {
  '1': [
    { id: '1', ideaId: '1', senderId: '2', senderName: 'Bob Investor', senderRole: 'investor', content: 'I love this concept! How far along is the prototype?', timestamp: '2024-01-15T10:30:00' },
    { id: '2', ideaId: '1', senderId: '3', senderName: 'Carol Innovator', senderRole: 'innovator', content: 'We have a working MVP with 85% accuracy on sorting.', timestamp: '2024-01-15T10:35:00' },
    { id: '3', ideaId: '1', senderId: '2', senderName: 'Bob Investor', senderRole: 'investor', content: 'Impressive! What is your funding target?', timestamp: '2024-01-15T10:40:00' },
  ],
  '2': [
    { id: '4', ideaId: '2', senderId: '4', senderName: 'David Investor', senderRole: 'investor', content: 'The urban farming space is growing rapidly.', timestamp: '2024-01-10T14:00:00' },
    { id: '5', ideaId: '2', senderId: '5', senderName: 'Eva Innovator', senderRole: 'innovator', content: 'Yes! We already have 20 farms in our pilot.', timestamp: '2024-01-10T14:05:00' },
  ],
};

export const projects: Project[] = [
  { id: '1', title: 'EdTech Rural Platform', description: 'Offline-first education platform now serving 5000 students', originIdeaId: '5', progress: 75, status: 'in-progress', teamMembers: ['Carol Innovator', 'Bob Investor', 'Tech Team'], startDate: '2024-01-01' },
  { id: '2', title: 'Senior Care Connect', description: 'Mobile app connecting 200+ seniors with caregivers', originIdeaId: '3', progress: 90, status: 'in-progress', teamMembers: ['Carol Innovator', 'Healthcare Partners'], startDate: '2023-11-15' },
  { id: '3', title: 'Farm2Table Network', description: 'Urban farming network pilot in 3 cities', originIdeaId: '2', progress: 40, status: 'planning', teamMembers: ['Eva Innovator', 'David Investor'], startDate: '2024-02-01' },
  { id: '4', title: 'GreenSort AI', description: 'AI recycling system deployed in 10 locations', originIdeaId: '1', progress: 25, status: 'planning', teamMembers: ['Carol Innovator'], startDate: '2024-03-01' },
  { id: '5', title: 'EnergyTrade Platform', description: 'Renewable energy credit marketplace', originIdeaId: '4', progress: 100, status: 'completed', teamMembers: ['Eva Innovator', 'Energy Partners'], startDate: '2023-06-01' },
];

export const badges: Badge[] = [
  { id: 'pioneer', name: 'Pioneer', description: 'One of the first 100 users', icon: '🚀' },
  { id: 'leader', name: 'Community Leader', description: 'Helped 10+ users', icon: '👑' },
  { id: 'first-investment', name: 'First Investment', description: 'Made your first investment', icon: '💰' },
  { id: 'idea-creator', name: 'Idea Creator', description: 'Submitted your first idea', icon: '💡' },
  { id: 'top-innovator', name: 'Top Innovator', description: 'Idea reached funding goal', icon: '⭐' },
  { id: 'collaborator', name: 'Collaborator', description: 'Joined 5+ collaborations', icon: '🤝' },
];

export const quizzes: Quiz[] = [
  { id: '1', title: 'Platform Basics', description: 'Learn how to use the platform', pointsReward: 50, questionCount: 10, isCompleted: false },
  { id: '2', title: 'Investment 101', description: 'Understand investment fundamentals', pointsReward: 100, questionCount: 15, isCompleted: false },
  { id: '3', title: 'Pitching Mastery', description: 'Test your pitching knowledge', pointsReward: 75, questionCount: 12, isCompleted: true },
  { id: '4', title: 'Legal Essentials', description: 'Know your legal basics', pointsReward: 80, questionCount: 10, isCompleted: false },
  { id: '5', title: 'Growth Strategies', description: 'Learn scaling techniques', pointsReward: 120, questionCount: 20, isCompleted: false },
];

export const dashboardMetrics: DashboardMetrics = {
  usersCount: 1247,
  ideasCount: 89,
  eventsCount: 12,
  projectsCount: 34,
  activeCollaborations: 23,
};

export const services: ServiceInfo[] = [
  { id: 'users', title: 'User Management', description: 'Connect with innovators and investors worldwide', icon: '👥', href: '/admin' },
  { id: 'forum', title: 'Forum', description: 'Engage in community discussions and share insights', icon: '💬', href: '/forum' },
  { id: 'events', title: 'Events & Courses', description: 'Attend workshops and learn from industry experts', icon: '📅', href: '/events' },
  { id: 'collaboration', title: 'Collaboration', description: 'Partner with others to bring ideas to life', icon: '🤝', href: '/collaboration' },
  { id: 'projects', title: 'Projects', description: 'Track progress of funded initiatives', icon: '📊', href: '/projects' },
  { id: 'achievements', title: 'Achievements', description: 'Earn badges and track your platform journey', icon: '🏆', href: '/achievements' },
];

// Data access functions
export const getUsers = () => users;
export const getUserById = (id: string) => users.find(u => u.id === id);
export const getIdeas = () => ideas;
export const getIdeaById = (id: string) => ideas.find(i => i.id === id);
export const getPosts = () => posts;
export const getPostById = (id: string) => posts.find(p => p.id === id);
export const getCommentsByPostId = (postId: string) => comments.filter(c => c.postId === postId);
export const getEvents = () => events;
export const getCourses = () => courses;
export const getProposals = () => proposals;
export const getChatMessages = (ideaId: string) => chatMessages[ideaId] || [];
export const getProjects = () => projects;
export const getBadges = () => badges;
export const getQuizzes = () => quizzes;
export const getDashboardMetrics = () => dashboardMetrics;
export const getServices = () => services;

export const mockDataStore: MockDataStore = {
  users,
  ideas,
  posts,
  comments,
  events,
  courses,
  proposals,
  chatMessages,
  projects,
  badges,
  quizzes,
  dashboardMetrics,
  services,
};
