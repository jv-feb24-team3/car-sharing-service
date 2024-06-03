package ua.team3.carsharingservice.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.team3.carsharingservice.exception.NotificationSendingException;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.telegram.TelegramBot;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final TelegramBot telegramBot;

    @Override
    public void sendRentalCreatedNotification(Rental rental) {
        SendMessage response = new SendMessage();
        String message = buildRentalCreatedMessage(rental);
        response.setText(message);
        response.setChatId(telegramBot.getAdminChatId());
        try {
            telegramBot.execute(response);
        } catch (TelegramApiException e) {
            throw new NotificationSendingException("Failed to send notification about "
                    + "creating rental with id: " + rental.getId());
        }
    }

    private String buildRentalCreatedMessage(Rental rental) {
        return String.format("New rental with id %d was created: "
                        + "Rental date %s, Car: %s, User: %s - ID: %d, Return date: %s",
                rental.getId(),
                rental.getRentalDate(),
                rental.getCar().getBrand(),
                rental.getUser().getEmail(),
                rental.getUser().getId(),
                rental.getReturnDate());
    }
}
