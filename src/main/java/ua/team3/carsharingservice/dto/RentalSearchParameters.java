package ua.team3.carsharingservice.dto;

import java.beans.ConstructorProperties;
import lombok.Data;

@Data
public class RentalSearchParameters {
    private Boolean isActive;
    private Long[] userIds;

    @ConstructorProperties({"is_active", "user_id"})
    public RentalSearchParameters(Boolean isActive, Long[] userIds) {
        this.isActive = isActive;
        this.userIds = userIds;
    }
}
