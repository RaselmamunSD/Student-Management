package com.example.Student.Management.service;

import com.example.Student.Management.Model.Enrollment;
import com.example.Student.Management.Model.EnrollmentStatus;
import com.example.Student.Management.Model.Student;
import com.example.Student.Management.util.GradeUtil;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranscriptPdfService {

    public byte[] buildTranscript(Student student) {
        List<Enrollment> rows = student.getEnrollments().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.APPROVED)
                .sorted(Comparator.comparing(e -> e.getSemester() != null ? e.getSemester() : ""))
                .collect(Collectors.toList());

        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            document.add(new Paragraph("Academic Transcript", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Student: " + student.getName()));
            document.add(new Paragraph("Email: " + student.getEmail()));
            document.add(new Paragraph("Department: " + student.getDepartment()));
            document.add(new Paragraph("Program: " + (student.getProgram() != null ? student.getProgram() : "—")));
            document.add(new Paragraph(String.format("CGPA: %.2f", student.getCgpa())));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            addHeader(table, "Course");
            addHeader(table, "Credits");
            addHeader(table, "Semester");
            addHeader(table, "Marks");
            addHeader(table, "Grade");
            addHeader(table, "Points");

            for (Enrollment e : rows) {
                double marks = e.getMarks() != null ? e.getMarks() : 0;
                String letter = e.getMarks() != null ? GradeUtil.marksToLetter(marks) : "—";
                double pts = e.getMarks() != null ? GradeUtil.marksToGradePoint(marks) : 0;

                table.addCell(cell(e.getCourse().getCourseName()));
                table.addCell(cell(String.valueOf(e.getCourse().getCredits())));
                table.addCell(cell(e.getSemester() != null ? e.getSemester() : "—"));
                table.addCell(cell(e.getMarks() != null ? String.format("%.1f", marks) : "—"));
                table.addCell(cell(e.getMarks() != null ? letter : "—"));
                table.addCell(cell(e.getMarks() != null ? String.format("%.2f", pts) : "—"));
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build transcript PDF", e);
        }
    }

    private static void addHeader(PdfPTable table, String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        c.setBackgroundColor(new Color(230, 240, 255));
        table.addCell(c);
    }

    private static PdfPCell cell(String text) {
        return new PdfPCell(new Phrase(text != null ? text : "", FontFactory.getFont(FontFactory.HELVETICA, 9)));
    }
}
