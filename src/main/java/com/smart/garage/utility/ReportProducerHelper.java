package com.smart.garage.utility;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.licensing.base.LicenseKey;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.models.*;
import com.smart.garage.services.contracts.EmailService;
import com.smart.garage.services.contracts.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportProducerHelper {

    private final ForexCurrencyExchange forexCurrencyExchange;
    private final EmailService emailService;
    private final VisitService visitService;

    public static final String DEST = "reports/";

    @Autowired
    public ReportProducerHelper(ForexCurrencyExchange forexCurrencyExchange, EmailService emailService, VisitService visitService) {
        this.forexCurrencyExchange = forexCurrencyExchange;
        this.emailService = emailService;
        this.visitService = visitService;
    }

    public void generateReport(User requester, int[] visitIDs, Currencies selectedCurrency) throws IOException {
        LicenseKey.loadLicenseFile(new File("itext-license-key.json"));

        List<Visit> visitList = getAllVisits(requester, visitIDs);

        if (requester.getRole().getName() == UserRoles.CUSTOMER) {
            checkValidVisitSelection(requester, visitList);
        }

        int totalCostForAllVisits = visitList.stream().map(Visit::getTotalCost).reduce(0, Integer::sum);
        double convertedTotalCost = forexCurrencyExchange.convertPriceFromBGNToForeignCurrency(selectedCurrency, totalCostForAllVisits);

        StringBuilder htmlReport = new StringBuilder();
        htmlReport.append(getHTMLStringStart(LocalDateTime.now()));
        htmlReport.append(getHTMLGeneralDetails(visitList.size(), convertedTotalCost, selectedCurrency, requester));

        visitList.forEach(visit -> {
            try {
                htmlReport.append(getVisitSection(requester, visit, selectedCurrency));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        htmlReport.append(getHtmlPageEnd());

        String documentName = DEST + "Visit-report-for-" + requester.getUsername() + ".pdf";
        File reportPDF = new File(documentName);

        OutputStream fileOutputStream = new FileOutputStream(documentName);
        HtmlConverter.convertToPdf(htmlReport.toString(), fileOutputStream);

        String emailContent = emailService.buildReportEmail(requester);
        emailService.send(requester.getEmail(), emailContent, "Visit Report", reportPDF);

        if (reportPDF.exists()) {
            reportPDF.delete();
        }
    }

    private void checkValidVisitSelection(User requester, List<Visit> visitList) {
        if (!visitList.stream().allMatch(e -> e.getVehicle().getUser().getId() == requester.getId())) {
            throw new InvalidParameter("Customers can only request reports for their personal vehicles.");
        }
    }

    private List<Visit> getAllVisits(User requester, int[] visitIDs) {
        List<Visit> visitList = new ArrayList<>();

        for (int visitID : visitIDs) {
            visitList.add(visitService.getById(requester, visitID));
        }

        return visitList;
    }

    private String getHTMLStringStart(LocalDateTime dateOfReportRequest) {
        return "<html>\n" +
                "<head>\n" +
                "    <title>HTML to PDF</title>\n" +
                "    <style>\n" +
                "        * {\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "\n" +
                "        /* Create two equal columns that floats next to each other */\n" +
                "        .column {\n" +
                "            float: left;\n" +
                "            width: 50%;\n" +
                "            padding: 10px;\n" +
                "        }\n" +
                "\n" +
                "        /* Clear floats after the columns */\n" +
                "        .row:after {\n" +
                "            content: \"\";\n" +
                "            display: table;\n" +
                "            clear: both;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"applicationForm\" style=\"padding: 20px; position: relative; font-family: Lato, sans-serif\">\n" +
                "    <h2 style=\"font-size: 20px; color: rgb(0, 0, 0); font-weight: bold; margin: 5px 0px; text-align: center;\">\n" +
                "        TDDV SMART GARAGE\n" +
                "    </h2>\n" +
                "    <h2 style=\"font-size: 20px; color: rgb(0, 0, 0); font-weight: bold;margin: 5px 0px;text-align: center;\">\n" +
                "        VISIT REPORT\n" +
                "    </h2>\n" +
                "    <h2 style=\"font-size: 20px;color: rgb(0, 0, 0);font-weight: bold;margin: 20px 0px;text-align: center;\">\n" +
                "        " + dateOfReportRequest.toLocalDate().toString() + "\n" +
                "    </h2>";
    }

    private String getHTMLGeneralDetails(int numberOfVisits, double totalCost, Currencies selectedCurrency, User requester) {
        return "\n" +
                "    <h4 style=\"padding: 20px 0px 20px 10px; font-size: 20px; color: rgb(255, 255, 255); background: grey; margin-top: 30px;\">\n" +
                "        General Details\n" +
                "    </h4>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Visits included\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + numberOfVisits + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Total cost\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + String.format("%.2f", totalCost) + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Currency\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + selectedCurrency + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Username\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + requester.getUsername() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Email\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + requester.getEmail() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Phone number\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + requester.getPhoneNumber() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "\n";
    }

    private String getVisitSection(User requester, Visit visit, Currencies selectedCurrency) throws IOException {
        double convertedVisitCost = forexCurrencyExchange.convertPriceFromBGNToForeignCurrency(selectedCurrency, visit.getTotalCost());

        List<String> services = visit.displayServices().stream().map(ServiceRecord::getServiceName).collect(Collectors.toList());
        String servicesString = String.join(", ", services);

        String startDate = String.format("%d-%s-%d", visit.getStartDate().getYear(), visit.getStartDate().getMonth(), visit.getStartDate().getDayOfMonth());
        String endDate = visit.getEndDate() == null ? "-" : String.format("%d-%s-%d", visit.getEndDate().getYear(), visit.getEndDate().getMonth(), visit.getEndDate().getDayOfMonth());

        StringBuilder visitSection = new StringBuilder("\n" +
                "    <p style=\"padding: 20px 0px 20px 10px; font-size: 20px; color: rgb(255, 255, 255); background: grey; margin-top: 30px;page-break-before: auto\">\n" +
                "        Visit ID: \n" + visit.getId() +
                "    </p>\n" +
                "\n");

        if (requester.getRole().getName() != UserRoles.CUSTOMER) {
            visitSection.append(addVehicleOwnerSection(visit));
        }

        visitSection.append(addVehicleSection(visit, convertedVisitCost, selectedCurrency, servicesString, endDate, startDate));

        return visitSection.toString();
    }

    private String addVehicleSection(Visit visit, Double convertedVisitCost, Currencies selectedCurrency,
                                     String servicesString, String endDate, String startDate) {
        return "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            License plate\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + visit.getVehicle().getLicense() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Vehicle Identification Number\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + visit.getVehicle().getVIN() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Make\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + visit.getVehicle().getModel().getMake().getName() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Model\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + visit.getVehicle().getModel().getName() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Year of manufacture\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + visit.getVehicle().getYear() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Start date\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + startDate + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            End date\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + endDate + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Status\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + visit.getStatus().getName() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Services\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + servicesString + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Cost for visit\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + String.format("%.2f ", convertedVisitCost) + selectedCurrency + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "\n";
    }

    private String addVehicleOwnerSection(Visit visit) {
        return "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Owner username\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + visit.getUser().getUsername() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Owner email\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + visit.getUser().getEmail() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"display: flex; align-items: center; text-align: start;\n" +
                "            margin-left: 0px; margin-right: 0px; border-bottom: 1px solid rgb(0, 0, 0);\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 50%; margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            Owner phone number\n" +
                "        </p>\n" +
                "        <p style=\"font-size: 16px; color: rgb(140, 140, 140); margin-top: 10px; margin-bottom: 10px;\">\n" +
                "            " + visit.getUser().getPhoneNumber() + "\n" +
                "        </p>\n" +
                "    </div>\n" +
                "\n";
    }

    private String getHtmlPageEnd() {
        return "<div style=\"display: flex; align-items: stretch; text-align: center; margin-left: 0px; margin-right: 0px;\n" +
                "            margin-top: 20px; flex-flow: row; place-content: flex-end;\">\n" +
                "        <p style=\"font-size: 16px; font-weight: 600; margin-left: 10px; width: 41.6667%; margin-top: 75px;\">\n" +
                "            SIGNATURE OF CHIEF EXECUTIVE\n" +
                "        </p>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }

}
