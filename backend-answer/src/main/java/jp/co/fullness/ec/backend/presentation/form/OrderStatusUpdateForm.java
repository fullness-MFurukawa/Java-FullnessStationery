package jp.co.fullness.ec.backend.presentation.form;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderStatusUpdateForm {

    private Integer orderId;

    @NotNull(message = "新しいステータスを選択してください")
    private Integer newStatusId;
}
