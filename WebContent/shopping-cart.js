$(document).ready(function () {
    // Create an object to store item quantities
    var itemQuantities = {};
    var totalPrice = 0;
    // Function to retrieve the shopping cart items
    function getShoppingCartItems() {
        $.get('api/index', function (data) {
            let dataJson = JSON.parse(data);
            // Clear the cart table
            $('#cart-table-body').empty();
            console.log(dataJson);
            console.log(dataJson["previousItems"]);
            // Calculate total price
            totalPrice = 0
            // Populate the cart table
            dataJson["previousItems"].forEach(function (item) {
                var movieTitle = item;
                var price = 100; // Default price per item
                var row = $('<tr>');
                row.append($('<td>').text(movieTitle));
                var quantity = itemQuantities[movieTitle] || 1;
                var quantityCell = $('<td>');
                quantityCell.append(
                    $('<button>').addClass('btn btn-sm btn-secondary').text('-').click(function () {
                        // Decrease quantity
                        if (quantity > 1) {
                            quantity--;
                            updateCartTable(movieTitle, quantity);
                        }
                    })
                );
                quantityCell.append($('<span>').text(quantity));
                quantityCell.append(
                    $('<button>').addClass('btn btn-sm btn-secondary').text('+').click(function () {
                        // Increase quantity
                        quantity++;
                        updateCartTable(movieTitle, quantity);
                    })
                );
                row.append(quantityCell);
                row.append($('<td>').text('$' + price.toFixed(2)));
                row.append($('<td>').text('$' + (price * quantity).toFixed(2)));
                var deleteButton = $('<button>').addClass('btn btn-sm btn-danger').text('Delete');
                deleteButton.click(function () {
                    $.ajax({
                        url: 'api/index?item=' + movieTitle, // Adjust the URL to include the item
                        method: 'DELETE', // Ensure the method is set as 'DELETE'
                        success: function () {
                            getShoppingCartItems();
                        },
                    });
                });
                row.append($('<td>').append(deleteButton));
                $('#cart-table-body').append(row);
                totalPrice += price * quantity;
            });
            // Update the total price
            $('#total-price').text(totalPrice.toFixed(2));
        });
    }

    // Function to update the cart table with new quantity
    function updateCartTable(movieTitle, quantity) {
        // Update the quantity in the itemQuantities object
        itemQuantities[movieTitle] = quantity;
        // Update the cart table without making an additional AJAX request
        getShoppingCartItems();
    }

    // Initial update of the cart table
    getShoppingCartItems();

    // Proceed to payment button
    $('#proceed-to-payment').click(function () {
        // Get the total price (assuming you have the total price stored in a variable named totalPrice)
        const tot = totalPrice; // Replace this with your actual total price

        // Redirect to the payment page (payment.html) with the total price as a URL parameter
        window.location.href = `payment.html?totalPrice=${tot}`;
    });
});
