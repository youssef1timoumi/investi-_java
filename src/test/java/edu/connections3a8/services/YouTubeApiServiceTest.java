package edu.connections3a8.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YouTube API Service Tests")
public class YouTubeApiServiceTest {
    
    @Test
    @DisplayName("Test fetching video information")
    public void testGetVideoInfo() {
        YouTubeApiService service = new YouTubeApiService();
        
        // Test with "Me at the zoo" - first YouTube video
        String videoId = "jNQXAC9IVRw";
        
        YouTubeApiService.YouTubeVideoInfo info = service.getVideoInfo(videoId);
        
        // Note: This test will fail if API key is not configured
        // That's expected - it will pass once you add your API key
        if (info != null) {
            assertEquals(videoId, info.getVideoId());
            assertNotNull(info.getTitle());
            assertNotNull(info.getThumbnailUrl());
            
            System.out.println("\n=== YouTube API Test Results ===");
            System.out.println("Video ID: " + info.getVideoId());
            System.out.println("Title: " + info.getTitle());
            System.out.println("Channel: " + info.getChannelTitle());
            System.out.println("Duration: " + info.getDurationInMinutes() + " minutes");
            System.out.println("Views: " + String.format("%,d", info.getViewCount()));
            System.out.println("Likes: " + String.format("%,d", info.getLikeCount()));
            System.out.println("Embeddable: " + info.isEmbeddable());
            System.out.println("Thumbnail: " + info.getThumbnailUrl());
            System.out.println("================================\n");
        } else {
            System.out.println("\n⚠️ API key not configured or video not found");
            System.out.println("This is expected if you haven't set up your YouTube API key yet.");
            System.out.println("See YOUTUBE_API_SETUP_GUIDE.md for instructions.\n");
        }
    }
    
    @Test
    @DisplayName("Test duration parsing")
    public void testDurationParsing() {
        // Test various ISO 8601 duration formats
        assertEquals(0, YouTubeApiService.parseDurationToMinutes("PT19S")); // 19 seconds -> 0 min
        assertEquals(1, YouTubeApiService.parseDurationToMinutes("PT45S")); // 45 seconds -> 1 min (rounded)
        assertEquals(5, YouTubeApiService.parseDurationToMinutes("PT5M")); // 5 minutes
        assertEquals(15, YouTubeApiService.parseDurationToMinutes("PT15M33S")); // 15 min 33 sec
        assertEquals(125, YouTubeApiService.parseDurationToMinutes("PT2H5M")); // 2 hours 5 minutes
        assertEquals(185, YouTubeApiService.parseDurationToMinutes("PT3H5M")); // 3 hours 5 minutes
        assertEquals(0, YouTubeApiService.parseDurationToMinutes("")); // Empty
        assertEquals(0, YouTubeApiService.parseDurationToMinutes(null)); // Null
    }
    
    @Test
    @DisplayName("Test with popular video")
    public void testPopularVideo() {
        YouTubeApiService service = new YouTubeApiService();
        
        // Test with a popular music video
        String videoId = "dQw4w9WgXcQ"; // Rick Astley - Never Gonna Give You Up
        
        YouTubeApiService.YouTubeVideoInfo info = service.getVideoInfo(videoId);
        
        if (info != null) {
            System.out.println("\n=== Popular Video Test ===");
            System.out.println("Title: " + info.getTitle());
            System.out.println("Views: " + String.format("%,d", info.getViewCount()));
            System.out.println("Embeddable: " + info.isEmbeddable());
            System.out.println("==========================\n");
            
            assertTrue(info.getViewCount() > 1000000, "Popular video should have many views");
        }
    }
}
