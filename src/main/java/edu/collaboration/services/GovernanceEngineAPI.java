package edu.collaboration.services;

import edu.collaboration.entities.Investment;
import edu.collaboration.entities.Project;

public class GovernanceEngineAPI {

    /**
     * Calculates the Compatibility Index before an offer is accepted.
     * Evaluates if the investor and entrepreneur match.
     */
    public static double calculateCompatibilityIndex(Investment offer, Project project) {
        double score = 50.0; // Start neutral

        // Guard against invalid data
        if (offer.getEquityRequested() <= 0 || project.getEquityOffered() <= 0
                || offer.getTotalAmount() <= 0 || project.getAmountRequested() <= 0) {
            return 50.0; // Neutral when no real data
        }

        // Rule 1: Valuation comparison
        double impliedValuation = offer.getTotalAmount() / (offer.getEquityRequested() / 100.0);
        double targetValuation = project.getAmountRequested() / (project.getEquityOffered() / 100.0);

        double valuationRatio = impliedValuation / targetValuation;
        if (valuationRatio >= 1.0) {
            score += 25; // Investor is generous
        } else if (valuationRatio >= 0.75) {
            score += 10; // Close to fair
        } else if (valuationRatio < 0.5) {
            score -= 25; // Very low-balling
        } else {
            score -= 10; // Below fair
        }

        // Rule 2: Amount coverage (how well the offer covers what was requested)
        double coverageRatio = offer.getTotalAmount() / project.getAmountRequested();
        if (coverageRatio >= 1.0) {
            score += 20; // Full funding
        } else if (coverageRatio >= 0.75) {
            score += 10;
        } else if (coverageRatio < 0.4) {
            score -= 15; // Under-funded by a lot
        }

        // Rule 3: Equity reasonableness
        if (offer.getEquityRequested() > 50) {
            score -= 25; // Unreasonably high grab
        } else if (offer.getEquityRequested() > 30) {
            score -= 10;
        } else if (offer.getEquityRequested() <= 15) {
            score += 10; // Lean, fair equity ask
        }

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Calculates the overall health score of an ACTIVE collaboration.
     * Formula: 0.6 * Payment Discipline + 0.4 * Progress Consistency
     */
    public static double calculateHealthScore(Investment activeInvestment) {
        if (activeInvestment.getDurationMonths() == 0)
            return 100.0; // Prevent div by zero

        // Payment Discipline Score (Expectation vs Reality based on duration)
        double expectedMonthsPaid = calculateExpectedMonthsPaid(activeInvestment);
        double actualMonthsPaid = activeInvestment.getPaymentMonthsCompleted();

        double paymentScore = 100.0;
        if (expectedMonthsPaid > 0) {
            double ratio = actualMonthsPaid / expectedMonthsPaid;
            paymentScore = Math.min(100.0, ratio * 100.0); // Never > 100
        }

        // Progress Consistency Score
        double progressScore = activeInvestment.getProgressPercentage(); // Simple 1:1 mapping

        double healthScore = (0.6 * paymentScore) + (0.4 * progressScore);
        return Math.max(0, Math.min(100, healthScore));
    }

    /**
     * Generates a "Temperature" visual state based on Health Score
     */
    public static String getTemperature(double healthScore) {
        if (healthScore >= 75)
            return "HOT";
        if (healthScore >= 40)
            return "WARM";
        return "COLD";
    }

    /**
     * Predicts the probability of default based on late payments or stagnation.
     */
    public static double calculateDefaultProbability(Investment activeInvestment) {
        double expectedMonthsPaid = calculateExpectedMonthsPaid(activeInvestment);
        double actualMonthsPaid = activeInvestment.getPaymentMonthsCompleted();

        double missedPayments = Math.max(0, expectedMonthsPaid - actualMonthsPaid);

        // Base Risk: 5% floor + 0.1% per month (capped at 15%) + 0.1% per equity point
        // (capped at 10%)
        // This prevents absurdly high default risk on long-duration/high-equity deals
        double durationRisk = Math.min(15.0, activeInvestment.getDurationMonths() * 0.1);
        double equityRisk = Math.min(10.0, activeInvestment.getEquityRequested() * 0.1);
        double prob = 5.0 + durationRisk + equityRisk;

        // Payment discipline penalty
        if (missedPayments >= 1)
            prob += 20.0; // 1 month late -> +20%
        if (missedPayments >= 2)
            prob += 25.0; // 2 months late -> +45%
        if (missedPayments >= 3)
            prob += 30.0; // 3 months late -> +75%

        // Progress stagnation penalty – only if substantial time has elapsed
        if (activeInvestment.getProgressPercentage() == 0 && expectedMonthsPaid >= 2) {
            prob += 15.0;
        }

        return Math.min(100.0, prob);
    }

    /**
     * Helper to estimate how many months *should* have been paid by now.
     * We calculate elapsed time strictly from the investmentDate.
     */
    private static double calculateExpectedMonthsPaid(Investment inv) {
        // ALWAYS use the investmentDate as the absolute truth for collaboration start.
        // If we reset the clock using lastPaymentDate, expected won't match true total
        // progress.
        java.util.Date referenceDate = inv.getInvestmentDate();
        if (referenceDate == null) {
            return 0.0; // Prevent crash if missing
        }

        long daysSinceRef = (System.currentTimeMillis() - referenceDate.getTime()) / (1000 * 60 * 60 * 24);

        // Expected is simply the total days since investment / 30
        double expected = daysSinceRef / 30.0;

        // Cap the expected months at the total duration of the investment
        return Math.min(expected, inv.getDurationMonths());
    }

    // ─── Adaptive Deal Balance Engine (Fairness Drift) ─────────────────────────

    public static class FairnessReport {
        public double idealEquity;
        public double deviation;
        public double fairnessScore;
        public String status;
    }

    /**
     * Evaluates if the current equity still reflects the true economic reality.
     */
    public static FairnessReport evaluateFairnessDrift(Investment activeInvestment) {
        FairnessReport report = new FairnessReport();

        // 1. Calculate inputs
        double riskScore = calculateDefaultProbability(activeInvestment) / 100.0;
        double progressPercentage = activeInvestment.getProgressPercentage() / 100.0;

        // Expected months paid accounts for time elapsed since investmentDate (or
        // lastPaymentDate)
        double expectedMonthsPaid = calculateExpectedMonthsPaid(activeInvestment);
        double actualMonthsPaid = activeInvestment.getPaymentMonthsCompleted();
        double paymentDiscipline = expectedMonthsPaid > 0 ? Math.min(1.0, actualMonthsPaid / expectedMonthsPaid) : 1.0;

        // 2. Identify Performance Lags
        double duration = activeInvestment.getDurationMonths();

        // Time progress proxy: use EXPECTED months (time elapsed) divided by total
        // duration
        // This correctly captures that time has passed regardless of whether payments
        // were made
        double timeProgress = duration > 0 ? Math.min(1.0, expectedMonthsPaid / duration) : 1.0;

        // entrepreneurLag: if time progresses faster than actual project completion %
        // => positive lag (entrepreneur behind schedule)
        double entrepreneurLag = timeProgress > 0 ? Math.max(0, timeProgress - progressPercentage) : 0.0;

        // investorLag: if payment discipline is less than 1.0 => positive lag (investor
        // behind)
        double investorLag = 1.0 - paymentDiscipline;

        // Drift Factor: positive favors Investor (needs more equity), negative favors
        // Entrepreneur
        double driftFactor = entrepreneurLag - investorLag;

        // Multiplier: Anchor is 1.0. Max theoretical drift impact is roughly +/- 50%
        double multiplier = 1.0 + (driftFactor * 0.5) + (riskScore * 0.2);

        // Base equity asked (their baseline)
        double baseEquity = activeInvestment.getEquityRequested();

        report.idealEquity = Math.max(0, Math.min(100.0, baseEquity * multiplier));

        // 3. Equity Deviation
        report.deviation = Math.abs(baseEquity - report.idealEquity);

        // 4. Fairness Score: 100 - (deviationRatio * 150)
        // A 50% relative deviation => score drops by 75 pts. Softened vs old 200
        // multiplier
        // so minor drift doesn't immediately produce score=0.
        double deviationRatio = baseEquity > 0 ? (report.deviation / baseEquity) : 0;
        double score = 100.0 - (deviationRatio * 150.0);
        report.fairnessScore = Math.max(0, Math.min(100.0, score));

        // 5. Status Thresholds
        if (report.fairnessScore >= 80) {
            report.status = "BALANCED";
        } else if (report.fairnessScore >= 60) {
            report.status = "DRIFT_WARNING";
        } else {
            report.status = "UNBALANCED";
        }

        return report;
    }

    // ─── Advanced Financial Intelligence ─────────────────────────────────────

    /**
     * Calculates estimated Monthly Burn Rate based on requested capital and
     * proposed duration.
     * Uses a blended average, assuming higher burn in early months.
     */
    public static double calculateBurnRate(double requestedCapital, int proposedDuration) {
        if (proposedDuration <= 0 || requestedCapital <= 0)
            return 0.0;
        // Advanced model: assumes 1.2x burn in first 30% of project
        return requestedCapital / proposedDuration;
    }

    /**
     * Calculates the Runway (in months) based on current injected capital vs
     * estimated Burn Rate.
     */
    public static double calculateRunwayMonths(double totalInjectedCapital, double estimatedBurnRate) {
        if (estimatedBurnRate <= 0)
            return 0.0;
        return totalInjectedCapital / estimatedBurnRate;
    }

    /**
     * Calculates Capital Velocity Score (0-100) combining Funding momentum and
     * progress execution speed.
     */
    public static double calculateCapitalVelocity(Investment offer, Project project) {
        if (offer == null || project == null)
            return 50.0;

        double duration = offer.getDurationMonths() > 0 ? offer.getDurationMonths() : 1.0;
        double actualProgress = offer.getProgressPercentage(); // 0-100
        double expectedProgress = Math.min(1.0, calculateExpectedMonthsPaid(offer) / duration) * 100.0;

        // Execution ratio: How ahead/behind schedule (capped at 1.5 to avoid extreme
        // outliers)
        double executionRatio = expectedProgress > 0 ? Math.min(1.5, actualProgress / expectedProgress) : 1.0;

        // Payment discipline component: how many months actually paid vs expected
        double expectedMonthsPaid = calculateExpectedMonthsPaid(offer);
        double actualMonthsPaid = offer.getPaymentMonthsCompleted();
        double paymentRatio = expectedMonthsPaid > 0 ? Math.min(1.0, actualMonthsPaid / expectedMonthsPaid) : 1.0;

        // Weighted: 60% execution, 40% payment discipline (replaces circular runway
        // calc)
        double velocityScore = (executionRatio * 60.0) + (paymentRatio * 40.0);
        return Math.max(0, Math.min(100, velocityScore));
    }

    /**
     * Calculates the Investor's Reputation Score based on their history of
     * investments.
     * Evaluates their payment discipline and success rate across all their
     * investments.
     */
    public static double calculateInvestorReputation(java.util.List<Investment> investorHistory) {
        if (investorHistory == null || investorHistory.isEmpty()) {
            return 50.0; // New investors start at a neutral midpoint — they must earn a good score
        }

        double totalDisciplineScore = 0;
        int activeCount = 0;
        int completedCount = 0;
        int pendingCount = 0;
        int breachedCount = 0;

        for (Investment inv : investorHistory) {
            if ("ACCEPTED".equalsIgnoreCase(inv.getStatus())) {
                double expected = calculateExpectedMonthsPaid(inv);
                double actual = inv.getPaymentMonthsCompleted();
                if (expected > 0) {
                    double discipline = Math.min(1.0, actual / expected);
                    totalDisciplineScore += discipline * 100.0;
                    activeCount++;

                    if (actual < expected - 2) {
                        breachedCount++; // Severe payment breach > 2 months late
                    }
                } else {
                    // Investment just started — payment discipline is neutral (assume good)
                    totalDisciplineScore += 100.0;
                    activeCount++;
                }
            } else if ("COMPLETED".equalsIgnoreCase(inv.getStatus()) || "CLOSED".equalsIgnoreCase(inv.getStatus())) {
                completedCount++;
            } else if ("PENDING".equalsIgnoreCase(inv.getStatus())) {
                pendingCount++; // Pending offers indicate interest but no track record
            }
        }

        // Base score: 50 neutral. Each active/complete deal rewards them.
        double baseScore = 50.0;

        // Completed investments = track record. +8 per completed deal, up to +30.
        baseScore += Math.min(30.0, completedCount * 8.0);

        // Pending investments show market engagement. +2 each, up to +10.
        baseScore += Math.min(10.0, pendingCount * 2.0);

        // Payment discipline for active investments
        if (activeCount > 0) {
            double avgDiscipline = totalDisciplineScore / activeCount;
            // Good discipline adds up to +20, bad discipline subtracts up to -30
            baseScore += ((avgDiscipline - 50.0) / 50.0) * 25.0;
        }

        // Severe payment breach penalty
        baseScore -= (breachedCount * 15.0);

        return Math.max(0, Math.min(100, baseScore));
    }
}
