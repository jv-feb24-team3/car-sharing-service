package ua.team3.carsharingservice.service;

public interface BillingFormatter {
    String formBillingDetails(String carName,
                              String startDate,
                              String endDate,
                              long daysCount,
                              long dailyFee);
}
