package com.example.Student.Management.util;

import com.example.Student.Management.Model.Enrollment;
import com.example.Student.Management.Model.EnrollmentStatus;

import java.util.List;

public final class GradeUtil {

    private GradeUtil() {}

    public static double marksToGradePoint(double marks) {
        if (marks >= 93) {
            return 4.0;
        }
        if (marks >= 90) {
            return 3.7;
        }
        if (marks >= 87) {
            return 3.3;
        }
        if (marks >= 83) {
            return 3.0;
        }
        if (marks >= 80) {
            return 2.7;
        }
        if (marks >= 77) {
            return 2.3;
        }
        if (marks >= 73) {
            return 2.0;
        }
        if (marks >= 70) {
            return 1.7;
        }
        if (marks >= 60) {
            return 1.0;
        }
        return 0.0;
    }

    public static String marksToLetter(double marks) {
        if (marks >= 93) {
            return "A";
        }
        if (marks >= 90) {
            return "A-";
        }
        if (marks >= 87) {
            return "B+";
        }
        if (marks >= 83) {
            return "B";
        }
        if (marks >= 80) {
            return "B-";
        }
        if (marks >= 77) {
            return "C+";
        }
        if (marks >= 73) {
            return "C";
        }
        if (marks >= 70) {
            return "C-";
        }
        if (marks >= 60) {
            return "D";
        }
        return "F";
    }

    public static double computeCgpa(List<Enrollment> enrollments) {
        double totalPoints = 0;
        int totalCredits = 0;
        for (Enrollment e : enrollments) {
            if (e.getStatus() != EnrollmentStatus.APPROVED || e.getMarks() == null) {
                continue;
            }
            int c = e.getCourse().getCredits();
            double gp = marksToGradePoint(e.getMarks());
            totalPoints += gp * c;
            totalCredits += c;
        }
        if (totalCredits == 0) {
            return 0.0;
        }
        return Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }
}
