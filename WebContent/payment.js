$(document).ready(function() {
    // Function to get customer ID
    getCustomerIDAndHandlePayment();
});

function getCustomerIDAndHandlePayment() {
    // Retrieve the customer ID through an AJAX GET request
    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/login",
        success: function(resultData) {
            if (resultData['isLoggedIn'] === true) {
                const customerID = resultData['customerId'];
                if (customerID) {
                    // If logged in, handle the payment
                    handlePayment(customerID);
                } else {
                    alert('Customer ID not found');
                }
            }
        },
        error: function(xhr, status, error) {
            alert('Error retrieving customer ID: ' + error);
        }
    });
}

function handlePayment(customerID) {
    $('#payment-form').submit(function(event) {
        event.preventDefault();
        const firstName = $('#firstName').val();
        const lastName = $('#lastName').val();
        const creditCardNumber = $('#creditCardNumber').val();
        const expirationString = $('#expirationDate').val();
        const totalPrice = $('#totalPrice').val();

        // Convert the expiration string to a JavaScript Date object
        const expirationDate = new Date(expirationString);
        // Format the expiration date to match the SQL DATE format (YYYY-MM-DD)
        const formattedExpiration = expirationDate.toISOString().split('T')[0];

        $.ajax({
            type: 'POST',
            url: `api/payment?customerID=${customerID}&firstName=${firstName}&lastName=${lastName}&creditCardNumber=${creditCardNumber}&expirationDate=${formattedExpiration}`,
            contentType: 'application/json',
            success: function(response) {
                if (response.success) {
                    alert('Transaction Successful!');
                } else {
                    alert('Transaction Failed: ' + response.error);
                }
            },
            error: function(xhr, status, error) {
                alert('Error processing payment: ' + error);
            }
        });
    });
}

