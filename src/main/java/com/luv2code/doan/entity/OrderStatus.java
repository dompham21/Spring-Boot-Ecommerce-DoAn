package com.luv2code.doan.entity;

public enum OrderStatus {

    NEW {
        @Override
        public String defaultDescription() {
            return "Order was placed by the customer";
        }

        @Override
        public String getName() {
            return "New";
        }

    },

    CANCELLED {
        @Override
        public String defaultDescription() {
            return "Order was rejected";
        }

        @Override
        public String getName() {
            return "Cancelled";
        }
    },

    PROCESSING {
        @Override
        public String defaultDescription() {
            return "Order is being processed";
        }

        @Override
        public String getName() {
            return "Pending";
        }
    },

    PACKAGED {
        @Override
        public String defaultDescription() {
            return "Products were packaged";
        }

        @Override
        public String getName() {
            return "Ready for delivery";
        }
    },

    PICKED {
        @Override
        public String defaultDescription() {
            return "Shipper picked the package";
        }

        @Override
        public String getName() {
            return "Picked";
        }
    },

    SHIPPING {
        @Override
        public String defaultDescription() {
            return "Shipper is delivering the package";
        }

        @Override
        public String getName() {
            return "Shipping";
        }
    },

    DELIVERED {
        @Override
        public String defaultDescription() {
            return "Customer received products";
        }

        @Override
        public String getName() {
            return "Delivered";
        }
    },

    RETURNED {
        @Override
        public String defaultDescription() {
            return "Products were returned";
        }

        @Override
        public String getName() {
            return "Returned";
        }
    },

    PAID {
        @Override
        public String defaultDescription() {
            return "Customer has paid this order";
        }

        @Override
        public String getName() {
            return "Paid";
        }
    },

    REFUNDED {
        @Override
        public String defaultDescription() {
            return "Customer has been refunded";
        }

        @Override
        public String getName() {
            return "Refunded";
        }
    },

    RETURN_REQUESTED {
        @Override
        public String defaultDescription() {
            return "Customer sent request to return purchase";
        }

        @Override
        public String getName() {
            return "Request Return";
        }
    };

    public abstract String defaultDescription();

    public abstract String getName();
}