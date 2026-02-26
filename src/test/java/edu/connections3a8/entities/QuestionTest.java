package edu.connections3a8.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Question Entity Tests")
class QuestionTest {

    private Question question;

    @BeforeEach
    void setUp() {
        System.out.println("[DEBUG_LOG] Setting up Question test");
        question = new Question();
    }

    @Test
    @DisplayName("[TEST] Question - Default Constructor")
    void testDefaultConstructor() {
        System.out.println("[DEBUG_LOG] Testing Question default constructor");
        assertNotNull(question);
        assertEquals(0, question.getId());
        assertEquals(0, question.getQuizId());
        assertNull(question.getQuestionText());
    }

    @Test
    @DisplayName("[TEST] Question - Parameterized Constructor")
    void testParameterizedConstructor() {
        System.out.println("[DEBUG_LOG] Testing Question parameterized constructor");
        
        Question q = new Question(1L, "What is Java?", "A language", "A coffee", "An island", "All of the above", 4);
        
        assertEquals(1L, q.getQuizId());
        assertEquals("What is Java?", q.getQuestionText());
        assertEquals("A language", q.getOption1());
        assertEquals("A coffee", q.getOption2());
        assertEquals("An island", q.getOption3());
        assertEquals("All of the above", q.getOption4());
        assertEquals(4, q.getCorrectAnswer());
    }

    @Test
    @DisplayName("[TEST] Question - ID Getter and Setter")
    void testIdGetterSetter() {
        System.out.println("[DEBUG_LOG] Testing Question ID getter and setter");
        question.setId(100L);
        assertEquals(100L, question.getId());
    }

    @Test
    @DisplayName("[TEST] Question - Quiz ID Getter and Setter")
    void testQuizIdGetterSetter() {
        System.out.println("[DEBUG_LOG] Testing Question quizId getter and setter");
        question.setQuizId(5L);
        assertEquals(5L, question.getQuizId());
    }

    @Test
    @DisplayName("[TEST] Question - Question Text Getter and Setter")
    void testQuestionTextGetterSetter() {
        System.out.println("[DEBUG_LOG] Testing Question questionText getter and setter");
        question.setQuestionText("What is polymorphism?");
        assertEquals("What is polymorphism?", question.getQuestionText());
    }

    @Test
    @DisplayName("[TEST] Question - Options Getter and Setter")
    void testOptionsGetterSetter() {
        System.out.println("[DEBUG_LOG] Testing Question options getter and setter");
        
        question.setOption1("Option A");
        question.setOption2("Option B");
        question.setOption3("Option C");
        question.setOption4("Option D");
        
        assertEquals("Option A", question.getOption1());
        assertEquals("Option B", question.getOption2());
        assertEquals("Option C", question.getOption3());
        assertEquals("Option D", question.getOption4());
    }

    @Test
    @DisplayName("[TEST] Question - Correct Answer Getter and Setter")
    void testCorrectAnswerGetterSetter() {
        System.out.println("[DEBUG_LOG] Testing Question correctAnswer getter and setter");
        question.setCorrectAnswer(3);
        assertEquals(3, question.getCorrectAnswer());
    }

    @Test
    @DisplayName("[TEST] Question - Correct Answer Validation")
    void testCorrectAnswerRange() {
        System.out.println("[DEBUG_LOG] Testing Question correctAnswer range (1-4)");
        
        // Valid values
        question.setCorrectAnswer(1);
        assertEquals(1, question.getCorrectAnswer());
        
        question.setCorrectAnswer(4);
        assertEquals(4, question.getCorrectAnswer());
    }

    @Test
    @DisplayName("[TEST] Question - Timestamps Getter and Setter")
    void testTimestampsGetterSetter() {
        System.out.println("[DEBUG_LOG] Testing Question timestamps getter and setter");
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        question.setCreatedAt(now);
        question.setUpdatedAt(now);
        
        assertEquals(now, question.getCreatedAt());
        assertEquals(now, question.getUpdatedAt());
    }

    @Test
    @DisplayName("[TEST] Question - Equals Method")
    void testEquals() {
        System.out.println("[DEBUG_LOG] Testing Question equals method");
        
        Question q1 = new Question();
        q1.setId(1L);
        
        Question q2 = new Question();
        q2.setId(1L);
        
        Question q3 = new Question();
        q3.setId(2L);
        
        assertEquals(q1, q2);
        assertNotEquals(q1, q3);
        assertNotEquals(q1, null);
        assertNotEquals(q1, "Not a Question");
    }

    @Test
    @DisplayName("[TEST] Question - HashCode Method")
    void testHashCode() {
        System.out.println("[DEBUG_LOG] Testing Question hashCode method");
        
        Question q1 = new Question();
        q1.setId(1L);
        
        Question q2 = new Question();
        q2.setId(1L);
        
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    @DisplayName("[TEST] Question - ToString Method")
    void testToString() {
        System.out.println("[DEBUG_LOG] Testing Question toString method");
        
        question.setId(1L);
        question.setQuizId(5L);
        question.setQuestionText("Test Question");
        question.setCorrectAnswer(2);
        
        String result = question.toString();
        
        assertTrue(result.contains("Question{"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("quizId=5"));
        assertTrue(result.contains("questionText='Test Question'"));
        assertTrue(result.contains("correctAnswer=2"));
    }

    @Test
    @DisplayName("[TEST] Question - Complete Question Setup")
    void testCompleteQuestionSetup() {
        System.out.println("[DEBUG_LOG] Testing complete Question setup");
        
        question.setId(10L);
        question.setQuizId(3L);
        question.setQuestionText("What is encapsulation?");
        question.setOption1("Hiding implementation details");
        question.setOption2("Inheritance");
        question.setOption3("Polymorphism");
        question.setOption4("Abstraction");
        question.setCorrectAnswer(1);
        
        assertEquals(10L, question.getId());
        assertEquals(3L, question.getQuizId());
        assertEquals("What is encapsulation?", question.getQuestionText());
        assertEquals("Hiding implementation details", question.getOption1());
        assertEquals("Inheritance", question.getOption2());
        assertEquals("Polymorphism", question.getOption3());
        assertEquals("Abstraction", question.getOption4());
        assertEquals(1, question.getCorrectAnswer());
    }
}
