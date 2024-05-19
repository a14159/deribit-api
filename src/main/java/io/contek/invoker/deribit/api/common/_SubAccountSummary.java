package io.contek.invoker.deribit.api.common;

import java.math.BigDecimal;
import java.util.Map;

public class _SubAccountSummary {
    public String email;
    public String id;
    public String margin_model;
    public String type;
    public String username;
    public String system_name;
    public Map<String, _PortfolioPosition> portfolio;

    public static class _PortfolioPosition {
        public String currency;
        public BigDecimal equity;
        public BigDecimal spot_reserve;
        public BigDecimal initial_margin;
        public BigDecimal maintenance_margin;
    }
}
