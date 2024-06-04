package ua.team3.carsharingservice.telegram.service;

import ua.team3.carsharingservice.model.Rental;

public interface NotificationService {

    void sendRentalCreatedNotification(Rental rental);
}
