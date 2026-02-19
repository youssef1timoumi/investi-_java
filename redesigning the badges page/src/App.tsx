import { useState, useEffect, useRef } from 'react'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { 
  Moon, 
  Sun, 
  Clock, 
  Search, 
  Plus, 
  Trash2, 
  Eye, 
  ArrowLeft,
  Award,
  X,
  CheckCircle,
  AlertCircle,
  Info,
  Sparkles
} from 'lucide-react'
import './App.css'

gsap.registerPlugin(ScrollTrigger)

// Types
interface Badge {
  id: string
  name: string
  description: string
  pointsRequired: number
  createdAt: Date
}

type Theme = 'light' | 'dark' | 'auto'
type StatusType = 'success' | 'error' | 'info' | null

// Sample badges data
const sampleBadges: Badge[] = [
  { id: '1', name: 'First Steps', description: 'Complete your first task', pointsRequired: 0, createdAt: new Date() },
  { id: '2', name: 'Point Collector', description: 'Earn 100 points total', pointsRequired: 100, createdAt: new Date() },
  { id: '3', name: 'Overachiever', description: 'Complete 10 tasks in one day', pointsRequired: 0, createdAt: new Date() },
  { id: '4', name: 'Gold Member', description: 'Maintain premium status for 30 days', pointsRequired: 500, createdAt: new Date() },
  { id: '5', name: 'Team Player', description: 'Collaborate on 5 team projects', pointsRequired: 0, createdAt: new Date() },
  { id: '6', name: 'Master Achiever', description: 'Unlock all other badges', pointsRequired: 1000, createdAt: new Date() },
]

function App() {
  // Theme state
  const [theme, setTheme] = useState<Theme>('light')
  const [isAutoMode, setIsAutoMode] = useState(false)
  
  // Form state
  const [badgeName, setBadgeName] = useState('')
  const [description, setDescription] = useState('')
  const [pointsRequired, setPointsRequired] = useState('')
  
  // Badge list state
  const [badges, setBadges] = useState<Badge[]>(sampleBadges)
  const [searchQuery, setSearchQuery] = useState('')
  const [sortBy, setSortBy] = useState<'name' | 'points' | 'newest'>('newest')
  
  // Status state
  const [status, setStatus] = useState<{ message: string; type: StatusType }>({ message: '', type: null })
  
  // Refs for animations
  const headerRef = useRef<HTMLDivElement>(null)
  const titleRef = useRef<HTMLHeadingElement>(null)
  const formRef = useRef<HTMLDivElement>(null)
  const buttonsRef = useRef<HTMLDivElement>(null)
  const badgeListRef = useRef<HTMLDivElement>(null)
  const cardsRef = useRef<(HTMLDivElement | null)[]>([])

  // Theme handling
  useEffect(() => {
    const handleAutoTheme = () => {
      if (isAutoMode) {
        const hour = new Date().getHours()
        const isDark = hour < 6 || hour >= 18
        document.documentElement.classList.toggle('dark', isDark)
        setTheme(isDark ? 'dark' : 'light')
      }
    }
    
    handleAutoTheme()
    const interval = setInterval(handleAutoTheme, 60000)
    return () => clearInterval(interval)
  }, [isAutoMode])

  const toggleTheme = () => {
    setIsAutoMode(false)
    const newTheme = theme === 'light' ? 'dark' : 'light'
    setTheme(newTheme)
    document.documentElement.classList.toggle('dark', newTheme === 'dark')
  }

  const toggleAutoMode = () => {
    setIsAutoMode(!isAutoMode)
  }

  // GSAP Entrance Animations
  useEffect(() => {
    const ctx = gsap.context(() => {
      // Header entrance
      gsap.fromTo(headerRef.current,
        { y: -40, opacity: 0 },
        { y: 0, opacity: 1, duration: 0.8, ease: 'expo.out', delay: 0.1 }
      )

      // Title character animation
      if (titleRef.current) {
        const chars = titleRef.current.querySelectorAll('.char')
        gsap.fromTo(chars,
          { y: 40, opacity: 0, rotateX: -15 },
          { 
            y: 0, 
            opacity: 1, 
            rotateX: 0,
            duration: 0.6, 
            ease: 'back.out(1.7)', 
            stagger: 0.03,
            delay: 0.2
          }
        )
      }

      // Form entrance
      gsap.fromTo(formRef.current,
        { rotateY: -30, opacity: 0, x: -50 },
        { rotateY: 0, opacity: 1, x: 0, duration: 0.8, ease: 'expo.out', delay: 0.4 }
      )

      // Form fields stagger
      const formFields = formRef.current?.querySelectorAll('.form-field')
      if (formFields) {
        gsap.fromTo(formFields,
          { x: -30, opacity: 0 },
          { x: 0, opacity: 1, duration: 0.5, ease: 'expo.out', stagger: 0.1, delay: 0.6 }
        )
      }

      // Buttons entrance
      const buttons = buttonsRef.current?.querySelectorAll('.action-btn')
      if (buttons) {
        gsap.fromTo(buttons,
          { y: 60, scale: 0.5, rotateX: 45, opacity: 0 },
          { 
            y: 0, 
            scale: 1, 
            rotateX: 0, 
            opacity: 1, 
            duration: 0.6, 
            ease: 'back.out(1.7)', 
            stagger: 0.1,
            delay: 0.8
          }
        )
      }

      // Badge list entrance
      gsap.fromTo(badgeListRef.current,
        { y: 50, opacity: 0 },
        { y: 0, opacity: 1, duration: 0.7, ease: 'expo.out', delay: 1 }
      )

      // Cards entrance with stagger
      cardsRef.current.forEach((card, index) => {
        if (card) {
          gsap.fromTo(card,
            { rotateY: -90, x: 100, opacity: 0 },
            { 
              rotateY: 0, 
              x: 0, 
              opacity: 1, 
              duration: 0.6, 
              ease: 'expo.out',
              delay: 1.2 + index * 0.1,
              scrollTrigger: {
                trigger: card,
                start: 'top 90%',
                toggleActions: 'play none none none'
              }
            }
          )
        }
      })
    })

    return () => ctx.revert()
  }, [badges])

  // Status message handler
  const showStatus = (message: string, type: StatusType) => {
    setStatus({ message, type })
    setTimeout(() => setStatus({ message: '', type: null }), 4000)
  }

  // Form handlers
  const handleAddBadge = () => {
    if (!badgeName.trim()) {
      showStatus('Please enter a badge name', 'error')
      return
    }
    if (!description.trim()) {
      showStatus('Please enter a description', 'error')
      return
    }

    const newBadge: Badge = {
      id: Date.now().toString(),
      name: badgeName,
      description: description,
      pointsRequired: parseInt(pointsRequired) || 0,
      createdAt: new Date()
    }

    setBadges([newBadge, ...badges])
    handleClearForm()
    showStatus('Badge added successfully!', 'success')
  }

  const handleClearForm = () => {
    setBadgeName('')
    setDescription('')
    setPointsRequired('')
  }

  const handleClearSearch = () => {
    setSearchQuery('')
  }

  // Filter and sort badges
  const filteredBadges = badges.filter(badge => 
    badge.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    badge.pointsRequired.toString().includes(searchQuery)
  )

  const sortedBadges = [...filteredBadges].sort((a, b) => {
    switch (sortBy) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'points':
        return a.pointsRequired - b.pointsRequired
      case 'newest':
      default:
        return b.createdAt.getTime() - a.createdAt.getTime()
    }
  })

  // Split title into characters for animation
  const titleChars = 'Badge Management'.split('').map((char, i) => (
    <span key={i} className="char inline-block" style={{ display: char === ' ' ? 'inline' : 'inline-block' }}>
      {char === ' ' ? '\u00A0' : char}
    </span>
  ))

  return (
    <div className="min-h-screen pb-20">
      {/* Header */}
      <header 
        ref={headerRef}
        className="sticky top-0 z-50 px-6 py-5 md:px-9 transition-all duration-300"
        style={{ 
          background: 'rgba(255,255,255,0.9)',
          backdropFilter: 'blur(20px)',
          borderBottom: '1px solid rgba(226,230,237,0.5)'
        }}
      >
        <div className="max-w-6xl mx-auto flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <h1 
            ref={titleRef}
            className="text-2xl md:text-[28px] font-extrabold text-navy dark:text-white"
            style={{ textShadow: '0 2px 4px rgba(0,0,0,0.04)' }}
          >
            {titleChars}
          </h1>
          
          <div className="flex items-center gap-3">
            <button
              onClick={toggleTheme}
              className={`btn btn-secondary flex items-center gap-2 text-xs px-4 py-2 ${isAutoMode ? 'opacity-60' : ''}`}
            >
              {theme === 'dark' ? (
                <>
                  <Sun className="w-4 h-4 animate-rotate-subtle" /> Light
                </>
              ) : (
                <>
                  <Moon className="w-4 h-4 animate-rotate-subtle" /> Dark
                </>
              )}
            </button>
            
            <button
              onClick={toggleAutoMode}
              className={`btn btn-secondary flex items-center gap-2 text-xs px-4 py-2 ${isAutoMode ? 'ring-2 ring-gold' : ''}`}
            >
              <Clock className={`w-4 h-4 ${isAutoMode ? 'animate-icon-pulse' : ''}`} /> Auto
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-6 md:px-9 py-8 space-y-8">
        {/* Form Section */}
        <div ref={formRef} className="perspective-container">
          <div className="bg-surface/80 dark:bg-navy-light/80 backdrop-blur-sm rounded-xl p-6 md:p-8 border border-border-color shadow-card">
            <div className="grid grid-cols-1 md:grid-cols-[160px_1fr] gap-4 md:gap-6">
              {/* Badge Name */}
              <label className="form-field text-sm font-semibold text-text dark:text-white pt-2">
                Badge Name:
              </label>
              <div className="form-field">
                <input
                  type="text"
                  value={badgeName}
                  onChange={(e) => setBadgeName(e.target.value)}
                  placeholder="Enter badge name"
                  className="modern-input"
                />
              </div>

              {/* Description */}
              <label className="form-field text-sm font-semibold text-text dark:text-white pt-2">
                Description:
              </label>
              <div className="form-field">
                <textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Enter badge description"
                  className="modern-textarea"
                  rows={3}
                />
              </div>

              {/* Points Required */}
              <label className="form-field text-sm font-semibold text-text dark:text-white pt-2">
                Points Required:
              </label>
              <div className="form-field">
                <input
                  type="number"
                  value={pointsRequired}
                  onChange={(e) => setPointsRequired(e.target.value)}
                  placeholder="0"
                  min="0"
                  className="modern-input"
                />
                <p className="mt-2 text-xs text-text-muted">
                  Set to 0 for badges earned by actions (not points)
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div ref={buttonsRef} className="flex flex-wrap justify-center gap-3 md:gap-4">
          <button onClick={handleAddBadge} className="action-btn btn btn-primary flex items-center gap-2 min-w-[130px]">
            <Plus className="w-4 h-4" /> Add Badge
          </button>
          <button onClick={handleClearForm} className="action-btn btn btn-secondary flex items-center gap-2 min-w-[130px]">
            <Trash2 className="w-4 h-4" /> Clear Form
          </button>
          <button onClick={() => showStatus(`Showing all ${badges.length} badges`, 'info')} className="action-btn btn btn-secondary flex items-center gap-2 min-w-[130px]">
            <Eye className="w-4 h-4" /> View All
          </button>
          <button onClick={() => showStatus('Navigating back...', 'info')} className="action-btn btn btn-dark flex items-center gap-2 min-w-[110px]">
            <ArrowLeft className="w-4 h-4" /> Back
          </button>
        </div>

        {/* Status Message */}
        {status.type && (
          <div className={`flex items-center justify-center gap-2 py-3 px-6 rounded-xl ${
            status.type === 'success' ? 'bg-success/10 text-success' :
            status.type === 'error' ? 'bg-danger/10 text-danger' :
            'bg-steel/10 text-steel'
          }`}>
            {status.type === 'success' && <CheckCircle className="w-5 h-5" />}
            {status.type === 'error' && <AlertCircle className="w-5 h-5" />}
            {status.type === 'info' && <Info className="w-5 h-5" />}
            <span className="font-semibold">{status.message}</span>
          </div>
        )}

        {/* Separator */}
        <div className="relative h-px bg-gradient-to-r from-transparent via-border-color to-transparent">
          <div className="absolute inset-0 bg-gradient-to-r from-transparent via-gold/30 to-transparent animate-shimmer" />
        </div>

        {/* Badge List Section */}
        <div ref={badgeListRef} className="space-y-6">
          {/* Section Header */}
          <div className="flex items-center gap-3">
            <Sparkles className="w-5 h-5 text-gold animate-icon-pulse" />
            <h2 className="text-lg md:text-xl font-bold text-navy dark:text-white">Existing Badges</h2>
            <span className="text-sm text-text-muted">({sortedBadges.length})</span>
          </div>

          {/* Search and Sort */}
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-text-muted" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search by name or points..."
                className="modern-input pl-10"
              />
            </div>
            
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value as 'name' | 'points' | 'newest')}
              className="modern-input sm:w-44 cursor-pointer"
            >
              <option value="newest">Sort by Newest</option>
              <option value="name">Sort by Name</option>
              <option value="points">Sort by Points</option>
            </select>
            
            <button
              onClick={handleClearSearch}
              className="btn btn-secondary text-xs px-4 py-2 flex items-center gap-2"
            >
              <X className="w-4 h-4" /> Clear
            </button>
          </div>

          {/* Badge Cards */}
          <div className="bg-surface/50 dark:bg-navy-light/50 rounded-xl border border-border-color p-4 min-h-[400px] max-h-[700px] overflow-y-auto custom-scrollbar">
            {sortedBadges.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-20 text-text-muted">
                <Award className="w-16 h-16 mb-4 opacity-50 animate-float" />
                <p className="text-lg font-medium">No badges found</p>
                <p className="text-sm">Try adjusting your search or add a new badge</p>
              </div>
            ) : (
              <div className="space-y-3">
                {sortedBadges.map((badge, index) => (
                  <div
                    key={badge.id}
                    ref={(el) => { cardsRef.current[index] = el }}
                    className="badge-card group"
                    style={{ perspective: '1000px' }}
                  >
                    <div className="flex items-start gap-4">
                      <div className="flex-shrink-0 w-12 h-12 rounded-xl bg-gradient-to-br from-gold/20 to-gold/5 flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                        <Award className="w-6 h-6 text-gold" />
                      </div>
                      
                      <div className="flex-1 min-w-0">
                        <div className="flex items-start justify-between gap-3">
                          <div>
                            <h3 className="font-bold text-navy dark:text-white text-base group-hover:text-gold transition-colors duration-200">
                              {badge.name}
                            </h3>
                            <p className="text-sm text-text-muted mt-1 line-clamp-2">
                              {badge.description}
                            </p>
                          </div>
                          
                          <div className="points-badge flex-shrink-0 group-hover:scale-110 group-hover:rotate-3 transition-all duration-300">
                            {badge.pointsRequired === 0 ? (
                              <span className="flex items-center gap-1">
                                <Sparkles className="w-3 h-3" /> Action
                              </span>
                            ) : (
                              `${badge.pointsRequired} pts`
                            )}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  )
}

export default App
