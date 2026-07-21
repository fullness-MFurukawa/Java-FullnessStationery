package jp.co.fullness.ec.backend.presentation.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jp.co.fullness.ec.backend.domain.exception.DomainException;

import lombok.extern.slf4j.Slf4j;

/**
 * 全コントローラ共通の例外ハンドラ。未捕捉の例外を BP000 エラー画面へ寄せる。
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /** 業務例外(不正アクセス・存在しない対象など)。 */
    @ExceptionHandler(DomainException.class)
    public String handleDomain(DomainException e, Model model) {
        log.warn("業務エラー: {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "admin/error";
    }

    /** DB 障害。 */
    @ExceptionHandler(DataAccessException.class)
    public String handleDataAccess(DataAccessException e, Model model) {
        log.error("データアクセスエラー", e);
        model.addAttribute("errorMessage", "データの取得・更新に失敗しました");
        return "admin/error";
    }

    /** 想定外の例外。 */
    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception e, Model model) {
        log.error("システムエラー", e);
        model.addAttribute("errorMessage", "システムエラーが発生しました。管理者に連絡してください");
        return "admin/error";
    }
}