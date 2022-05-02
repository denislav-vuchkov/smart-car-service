package com.smart.garage.services;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.smart.garage.models.*;
import com.smart.garage.services.contracts.PaymentServices;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServicesImpl implements PaymentServices {

    private static final String CLIENT_ID = "AZvbonMB96AHkLveXRekdjQpveL3wCU6m7wkblyQrgB-BuCmSSzYdbs_Ccps3Nxs4BC9tqka96z1ZtMa";
    private static final String CLIENT_SECRET = "EEFKG1Yb_Z-LXZlb1NUZAd28fTG2qxl-os4NYM9nyB49MDZOV64be7f8Xe3CATbJhWZsxEcU_l00iCxM";
    private static final String MODE = "sandbox";

    @Override
    public String authorizePayment(PaypalOrder paypalOrder, User currentUser, Visit visit) throws PayPalRESTException, IOException {
        Payer payer = getPayerInformation(currentUser);
        RedirectUrls redirectUrls = getRedirectURLs(visit);
        List<Transaction> listTransaction = getTransactionInformation(paypalOrder, visit);

        Payment requestPayment = new Payment();
        requestPayment.setTransactions(listTransaction);
        requestPayment.setRedirectUrls(redirectUrls);
        requestPayment.setPayer(payer);
        requestPayment.setIntent("authorize");

        APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);

        Payment approvedPayment = requestPayment.create(apiContext);

        return getApprovalLink(approvedPayment);
    }

    @Override
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment payment = new Payment().setId(paymentId);

        APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);

        return payment.execute(apiContext, paymentExecution);
    }

    private Payer getPayerInformation(User currentUser) {
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        PayerInfo payerInfo = new PayerInfo();
        payerInfo.setFirstName("")
                .setLastName("")
                .setEmail(currentUser.getEmail());

        payer.setPayerInfo(payerInfo);

        return payer;
    }

    private RedirectUrls getRedirectURLs(Visit visit) {
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://smartgarage.shop/visits/" + visit.getId() + "/pay-with-paypal");
        redirectUrls.setReturnUrl("http://smartgarage.shop/visits/" + visit.getId() + "/pay-with-paypal/execute");

        return redirectUrls;
    }

    private List<Transaction> getTransactionInformation(PaypalOrder paypalOrder, Visit visit) throws IOException {
        Details details = new Details();
        details.setShipping(String.format("%.0f", paypalOrder.getShipping()));
        details.setSubtotal(String.format("%.0f", paypalOrder.getSubtotal()));
        details.setTax(String.format("%.0f", paypalOrder.getTax()));

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.format("%.0f", paypalOrder.getTotal()));
        amount.setDetails(details);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Services rendered by TDDV Smart Garage for vehicle with license plate: " + visit.getVehicle().getLicense());

        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();

        Item item = new Item();
        item.setCurrency("USD");
        item.setName(String.format("Services executed for vehicle with license plate %s between %s and %s date.",
                visit.getVehicle().getLicense(), visit.getStartDate(), visit.getEndDate()));
        item.setPrice(String.format("%.0f", paypalOrder.getTotal()));
        item.setTax(String.format("%.0f", paypalOrder.getTax()));
        item.setQuantity("1");

        items.add(item);

        itemList.setItems(items);
        transaction.setItemList(itemList);

        List<Transaction> listTransaction = new ArrayList<>();
        listTransaction.add(transaction);

        return listTransaction;
    }

    private String getApprovalLink(Payment approvedPayment) {
        List<Links> links = approvedPayment.getLinks();
        String approvalLink = null;

        for (Links link : links) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                approvalLink = link.getHref();
                break;
            }
        }

        return approvalLink;
    }

}
