package ua.team3.carsharingservice.telegram.service;

import java.time.LocalDate;
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

    @Override
    public void sendOverdueRentalsNotification(Rental rental) {
        SendMessage message = new SendMessage();
        message.setChatId(telegramBot.getAdminChatId());
        message.setText(buildOverdueRentalsList(rental));
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            throw new NotificationSendingException("Can't send notification about rental with id "
                    + rental.getId());
        }
    }

    public void sendNoOverdueRentalsNotification() {
        SendMessage response = new SendMessage();
        response.setChatId(telegramBot.getAdminChatId());
        response.setText("No overdue rentals today! - " + LocalDate.now());
        try {
            telegramBot.execute(response);
        } catch (TelegramApiException e) {
            throw new NotificationSendingException("Failed to send notification "
                    + "about no overdue rent");
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

    private String buildOverdueRentalsList(Rental rental) {
        return String.format("Rental with id: %d is overdue, User - %s %s(%s), return date - %s",
                rental.getId(),
                rental.getUser().getFirstName(),
                rental.getUser().getLastName(),
                rental.getUser().getEmail(),
                rental.getReturnDate());
    }
}
