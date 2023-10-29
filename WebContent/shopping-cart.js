$(document).ready(function () {
    function getShoppingCartItems() {
        $.get('api/index', function (data) {
            let dataJson = JSON.parse(data);
            $('#cart-table-body').empty();

            let totalPrice = 0;

            dataJson["previousItems"].forEach(function (item) {
                var movieTitle = item.item;
                var price = 100; // Default price per item
                var row = $('<tr>');
                row.append($('<td>').text(movieTitle));
                var quantity = item.quantity || 1;

                var quantityCell = $('<td>');
                quantityCell.append(
                    $('<button>').addClass('btn btn-sm btn-secondary').text('-').click(function () {
                        // Decrease quantity by 1
                        quantity = Math.max(quantity - 1, 1); // Ensure quantity doesn't go below 1
                        updateCartTable(movieTitle, quantity);
                        updateQuantityDisplay(quantityCell, quantity);
                    })
                );

                quantityCell.append($('<span>').text(quantity));

                quantityCell.append(
                    $('<button>').addClass('btn btn-sm btn-secondary').text('+').click(function () {
                        // Increase quantity by 1
                        quantity = quantity + 1;
                        updateCartTable(movieTitle, quantity);
                        updateQuantityDisplay(quantityCell, quantity);
                    })
                );

                row.append(quantityCell);
                row.append($('<td>').text('$' + price.toFixed(2)));
                row.append($('<td>').text('$' + (price * quantity).toFixed(2)));
                var deleteButton = $('<button>').addClass('btn btn-sm btn-danger').text('Delete');
                deleteButton.click(function () {
                    $.ajax({
                        url: 'api/index?item=' + movieTitle,
                        method: 'DELETE',
                        success: function () {
                            getShoppingCartItems();
                        },
                    });
                });
                row.append($('<td>').append(deleteButton));
                $('#cart-table-body').append(row);
                totalPrice += price * quantity;
            });

            $('#total-price').text(totalPrice.toFixed(2));
        });
    }

    function updateQuantityDisplay(cell, quantity) {
        cell.children('span').text(quantity);
    }

    function updateCartTable(movieTitle, quantity) {
        $.ajax({
            url: 'api/index?item=' + movieTitle + '&quantity=' + quantity,
            method: 'POST',
            success: function () {
                getShoppingCartItems();
            },
        });
    }

    getShoppingCartItems();

    $('#proceed-to-payment').click(function () {
        const tot = $('#total-price').text();
        window.location.href = `payment.html?totalPrice=${tot}`;
    });
});
