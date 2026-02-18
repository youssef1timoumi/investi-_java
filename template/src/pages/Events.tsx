import { useState, useCallback } from 'react';
import PageLayout from '../components/PageLayout';
import EventCard from '../components/EventCard';
import CourseCard from '../components/CourseCard';
import { getEvents, getCourses } from '../data/mockData';
import styles from './Events.module.css';

interface InscriptionState {
  [eventId: string]: boolean;
}

interface EnrollmentState {
  [courseId: string]: boolean;
}

export default function Events() {
  const events = getEvents();
  const courses = getCourses();

  const [inscriptions, setInscriptions] = useState<InscriptionState>({});
  const [enrollments, setEnrollments] = useState<EnrollmentState>({});
  const [activeTab, setActiveTab] = useState<'events' | 'courses'>('events');

  const handleInscribe = useCallback((eventId: string) => {
    setInscriptions((prev) => ({ ...prev, [eventId]: true }));
  }, []);

  const handleEnroll = useCallback((courseId: string) => {
    setEnrollments((prev) => ({ ...prev, [courseId]: true }));
  }, []);

  const totalEnrolled = courses.reduce((acc, c) => acc + c.enrolledCount, 0);

  return (
    <PageLayout backgroundColor="light">
      <div className={styles.eventsPage}>
        {/* Hero Section */}
        <div className={styles.heroSection}>
          <div className={styles.heroContent}>
            <h1 className={styles.heroTitle}>Events & Learning</h1>
            <p className={styles.heroSubtitle}>
              Join exclusive events, network with industry leaders, and enhance your skills with expert-led courses
            </p>
            
            {/* Quick Stats */}
            <div className={styles.heroStats}>
              <div className={styles.heroStat}>
                <span className={styles.heroStatValue}>{events.length}</span>
                <span className={styles.heroStatLabel}>Upcoming Events</span>
              </div>
              <div className={styles.heroStatDivider} />
              <div className={styles.heroStat}>
                <span className={styles.heroStatValue}>{courses.length}</span>
                <span className={styles.heroStatLabel}>Courses Available</span>
              </div>
              <div className={styles.heroStatDivider} />
              <div className={styles.heroStat}>
                <span className={styles.heroStatValue}>{totalEnrolled}+</span>
                <span className={styles.heroStatLabel}>Students Enrolled</span>
              </div>
            </div>
          </div>
        </div>

        <div className={styles.container}>
          {/* Tab Navigation */}
          <div className={styles.tabNav}>
            <button
              className={`${styles.tabButton} ${activeTab === 'events' ? styles.tabActive : ''}`}
              onClick={() => setActiveTab('events')}
            >
              <span className={styles.tabIcon}>📅</span>
              Events
              <span className={styles.tabBadge}>{events.length}</span>
            </button>
            <button
              className={`${styles.tabButton} ${activeTab === 'courses' ? styles.tabActive : ''}`}
              onClick={() => setActiveTab('courses')}
            >
              <span className={styles.tabIcon}>📚</span>
              Courses
              <span className={styles.tabBadge}>{courses.length}</span>
            </button>
          </div>

          {/* Events Section */}
          {activeTab === 'events' && (
            <section className={styles.section}>
              <div className={styles.sectionHeader}>
                <div>
                  <h2 className={styles.sectionTitle}>Upcoming Events</h2>
                  <p className={styles.sectionSubtitle}>
                    Connect with innovators, investors, and industry experts at our curated events
                  </p>
                </div>
              </div>

              <div className={styles.eventsGrid}>
                {events.map((event, index) => (
                  <div 
                    key={event.id} 
                    className={styles.eventCardWrapper}
                    style={{ animationDelay: `${index * 0.1}s` }}
                  >
                    <EventCard
                      event={event}
                      isInscribed={inscriptions[event.id] || false}
                      onInscribe={() => handleInscribe(event.id)}
                    />
                  </div>
                ))}
              </div>

              {events.length === 0 && (
                <div className={styles.emptyState}>
                  <span className={styles.emptyIcon}>📅</span>
                  <h3>No upcoming events</h3>
                  <p>Check back soon for new events!</p>
                </div>
              )}
            </section>
          )}

          {/* Courses Section */}
          {activeTab === 'courses' && (
            <section className={styles.section}>
              <div className={styles.sectionHeader}>
                <div>
                  <h2 className={styles.sectionTitle}>Expert-Led Courses</h2>
                  <p className={styles.sectionSubtitle}>
                    Enhance your skills with courses designed for innovators and investors
                  </p>
                </div>
              </div>

              <div className={styles.coursesGrid}>
                {courses.map((course, index) => (
                  <div 
                    key={course.id} 
                    className={styles.courseCardWrapper}
                    style={{ animationDelay: `${index * 0.1}s` }}
                  >
                    <CourseCard
                      course={course}
                      isEnrolled={enrollments[course.id] || false}
                      onEnroll={() => handleEnroll(course.id)}
                    />
                  </div>
                ))}
              </div>

              {courses.length === 0 && (
                <div className={styles.emptyState}>
                  <span className={styles.emptyIcon}>📚</span>
                  <h3>No courses available</h3>
                  <p>Check back soon for new courses!</p>
                </div>
              )}
            </section>
          )}
        </div>
      </div>
    </PageLayout>
  );
}
