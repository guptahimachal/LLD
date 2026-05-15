package org.example.report;

public class ReportFactory {

    public static Report generateReport(String type) {
        return switch (type) {
            case "PDF" -> new PdfReport();
            case "HTML" -> new HTMLReport();
            case "CSV" -> new CsvReport();
            default -> throw new IllegalArgumentException("Unsupported report type");
        };
    }

}
