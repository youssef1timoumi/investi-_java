# YouTube Integration Testing Guide

## Quick Test Steps

### Test 1: Working YouTube Video
1. Open Course Form (Admin/Teacher view)
2. Create a new course with these details:
   - Title: "Test YouTube Video"
   - Content URL: `https://www.youtube.com/watch?v=jNQXAC9IVRw`
   - Content Type: video
3. Save the course
4. Go to Course Catalog
5. Click "Course Content" on the test course
6. **Expected Result**: Video should embed and play

### Test 2: Short YouTube URL
1. Create another course
2. Use short URL: `https://youtu.be/jNQXAC9IVRw`
3. **Expected Result**: Video should embed and play

### Test 3: Embedding Restricted Video
1. Create a course with a restricted video URL
2. Open course content
3. **Expected Result**: 
   - Info message about embedding restrictions
   - "Open in Browser" button visible
   - Click button to watch on YouTube

### Test 4: Dark Mode
1. Open any course with YouTube video
2. Click "🌙 Night Mode" button
3. **Expected Result**:
   - Video container background changes to dark
   - Text colors update
   - Button styling updates
4. Toggle back to light mode
5. **Expected Result**: Colors revert to light theme

### Test 5: Invalid YouTube URL
1. Create course with invalid URL: `https://www.youtube.com/invalid`
2. Open course content
3. **Expected Result**: Error message displayed

### Test 6: Mixed Content Types
Create multiple courses:
- Course A: YouTube video
- Course B: Local MP4 file (`media/courses/course_xxx.mp4`)
- Course C: PDF file (`media/courses/course_xxx.pdf`)
- Course D: External URL

Open each course and verify correct player loads.

## What to Look For

### Success Indicators
✅ Video embeds and plays smoothly
✅ YouTube controls work (play, pause, volume, fullscreen)
✅ "Open in Browser" button always visible
✅ Dark mode styling applies correctly
✅ No console errors
✅ Video ID displays at bottom
✅ Info message about restrictions shows

### Potential Issues
❌ Video shows Error 153 → Use "Open in Browser" button
❌ Video doesn't load → Check internet connection
❌ Black screen → Video may be region-restricted
❌ WebView error → Check Java version (need Java 11+)

## Browser Fallback Test

1. Click "🌐 Open in Browser" button
2. **Expected Result**: 
   - Default web browser opens
   - YouTube video page loads
   - Can watch video on YouTube.com

## Error 153 Explanation

If you see "Erreur 153":
- This is YouTube's error, not the app
- Video creator disabled embedding
- Solution: Click "Open in Browser" button
- This is expected behavior for some videos

## Recommended Test Videos

### Videos That Should Work
- `https://www.youtube.com/watch?v=jNQXAC9IVRw` - "Me at the zoo" (first YouTube video)
- `https://www.youtube.com/watch?v=dQw4w9WgXcQ` - Popular music video
- Most educational/tutorial videos

### Videos That May Be Restricted
- Music videos from major labels
- TV show clips
- Movie trailers
- Some copyrighted content

## Performance Check

### Normal Performance
- Video loads within 2-3 seconds
- Smooth playback
- No lag when switching quality
- Responsive controls

### If Performance is Slow
- Check internet speed
- Close other browser tabs
- Lower video quality in YouTube player
- Consider using local video files instead

## Dark Mode Verification

### Light Mode Colors
- Container: White background
- Border: #456990 (Baltic Blue)
- Text: #000501 (Black)
- Button: #DC3545 (Red)

### Dark Mode Colors
- Container: #161630 (Dark blue)
- Border: rgba(70,70,100,0.6) (Muted blue)
- Text: #F0F2FA (Light)
- Button: #DC3545 (Red)

## Console Output

When testing, check console for these messages:
```
Found YouTube URL: [url]
Video ID extracted: [id]
Loading YouTube video...
```

If you see errors, they'll help diagnose issues.

## Success Criteria

✅ YouTube videos embed successfully
✅ Fallback button works for restricted videos
✅ Dark mode applies correctly
✅ No application crashes
✅ Clear error messages when needed
✅ Users can always access content (embed or browser)

## Troubleshooting

### Video Won't Load
1. Check internet connection
2. Try different YouTube URL
3. Click "Open in Browser" button
4. Check console for errors

### WebView Issues
1. Verify Java 11+ installed
2. Check JavaFX WebView dependencies
3. Restart application
4. Try different video

### Dark Mode Not Working
1. Toggle night mode button
2. Check ThemeManager singleton
3. Reload course content page
4. Verify CSS files loaded

## Next Steps After Testing

If everything works:
✅ YouTube integration is complete
✅ Users can watch videos in app or browser
✅ Error handling is robust
✅ Dark mode support is functional

If issues found:
1. Note specific error messages
2. Check which videos fail
3. Verify Java/JavaFX versions
4. Review console output
5. Test on different machines

## Additional Features to Test

### Navigation
- Back button works from video page
- Theme persists when navigating
- History tracks video courses

### Interactions
- Like/dislike buttons work
- Report button functions
- Quiz access from video course

### Responsive Design
- Video container scales properly
- Buttons are clickable
- Text is readable
- Layout doesn't break

## Final Checklist

- [ ] YouTube videos embed successfully
- [ ] Short URLs work
- [ ] "Open in Browser" button functions
- [ ] Dark mode styling correct
- [ ] Error messages clear
- [ ] No console errors
- [ ] Performance acceptable
- [ ] Navigation works
- [ ] Theme persists
- [ ] All content types load correctly

## Support

If you encounter issues:
1. Check `YOUTUBE_ERROR_153_FIX.md` for Error 153 details
2. Review `YOUTUBE_INTEGRATION_FEATURE.md` for full documentation
3. Verify Java and JavaFX versions
4. Test with different YouTube videos
5. Check internet connection and firewall settings
