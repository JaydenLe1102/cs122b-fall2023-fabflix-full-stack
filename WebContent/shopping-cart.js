$(document).ready(function () {
    function getShoppingCartItems() {
        $.get('api/index', function (data) {
            let dataJson = JSON.parse(data)
            $('#cart-table-body').empty()

            let totalPrice = 0

            dataJson['previousItems'].forEach(function (item) {
                var movieTitle = item.item
                var price = 100 // Default price per item
                var row = $('<tr>')
                row.append($('<td>').text(movieTitle))
                var quantity = Number(item.quantity) || 1

                console.log(item)

                var quantityCell = $('<td>')
                if (quantity > 1) {
                    quantityCell.append(
                        $('<button>')
                            .addClass('btn btn-sm btn-secondary')
                            .attr('id', 'decrease-quantity_' + item.item)
                            .text('-')
                            .click(function () {
                                // Decrease quantity by 1
                                console.log("hello quantity" + quantity)
                                //

                                if (quantity === 1) {
                                    document.getElementById('decrease-quantity_' + item.item).classList.add('disabled')
                                }
                                quantity = Math.max(quantity - 1, 1)

                                console.log(quantity)


                                updateCartTable(movieTitle, -1)
                                updateQuantityDisplay(quantityCell, quantity)
                            })
                    )
                } else {
                    quantityCell.append(
                        $('<button>')
                            .addClass('btn btn-sm btn-secondary disabled')
                            .attr('id', 'decrease-quantity_' + item.item)
                            .text('-')
                            .click(function () {
                                // Decrease quantity by 1
                                console.log("hello quantity" + quantity)
                                // quantity = Math.max(quantity - 1, 1) // Ensure quantity doesn't go below 1

                                quantity = Math.max(quantity - 1, 1)
                                console.log(quantity)

                                if (quantity === 1) {
                                    document.getElementById('decrease-quantity_' + item.item).classList.add('disabled')
                                }


                                updateCartTable(movieTitle, -1)
                                updateQuantityDisplay(quantityCell, quantity)
                            })
                    )
                }


                quantityCell.append($('<span>').text(quantity))

                quantityCell.append(
                    $('<button>')
                        .addClass('btn btn-sm btn-secondary')
                        .text('+')
                        .click(function () {
                            // Increase quantity by 1
                            if (quantity === 1) {
                                document.getElementById('decrease-quantity_' + item.item).classList.remove('disabled')
                            }
                            quantity = quantity + 1
                            console.log(quantity)
                            updateCartTable(movieTitle, quantity)
                            updateQuantityDisplay(quantityCell, quantity)
                        })
                )

                row.append(quantityCell)
                row.append($('<td>').text('$' + price.toFixed(2)))
                row.append($('<td>').text('$' + (price * quantity).toFixed(2)))
                var deleteButton = $('<button>')
                    .addClass('btn btn-sm btn-danger')
                    .text('Delete')
                deleteButton.click(function () {
                    $.ajax({
                        url: 'api/index?item=' + movieTitle,
                        method: 'DELETE',
                        success: function () {
                            getShoppingCartItems()
                        },
                    })
                })
                row.append($('<td>').append(deleteButton))
                $('#cart-table-body').append(row)
                totalPrice += price * quantity
            })
            $('#total-price').text(totalPrice.toFixed(2))
        })
    }

    function updateQuantityDisplay(cell, quantity) {
        cell.children('span').text(quantity)
    }

    function updateCartTable(movieTitle, quantity) {
        $.ajax({
            url: 'api/index?item=' + movieTitle + '&quantity=' + quantity,
            method: 'POST',
            success: function () {
                getShoppingCartItems()
            },
        })
    }

    getShoppingCartItems()

    $('#proceed-to-payment').click(function () {
        const tot = $('#total-price').text()
        window.location.href = `payment.html?totalPrice=${tot}`
    })
})

function handleLookup(query, doneCallback) {
    console.log("Autocomplete initiated");

    // Check if the query is in the cache
    var cachedData = localStorage.getItem(query);
    if (cachedData) {
        console.log("Using cached data for query: " + query);
        handleLookupAjaxSuccess(JSON.parse(cachedData), query, doneCallback);
    } else {
        console.log("Sending AJAX request to backend Java Servlet");
        // If not in cache, send the HTTP GET request to the Java Servlet endpoint hero-suggestion
        // with the query data
        jQuery.ajax({
            method: "GET",
            url: "api/autocomplete?query=" + escape(query),
            success: function (data) {
                // Store the data in the cache
                localStorage.setItem(query, JSON.stringify(data));

                // Pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback);
            },
            error: function (errorData) {
                console.log("Lookup AJAX error");
                console.log(errorData);
            },
        });
    }
}

function handleLookupAjaxSuccess(data, query, doneCallback) {

    // Assuming data is already an object, not a JSON string
    var jsonData = data.slice(0, 10);

    // Transform movie data into suggestion objects
    var suggestions = jsonData.map(function (movie) {
        return {
            value: movie.title,
            data: movie,
        };
    });

    console.log("Used suggestion list: ", suggestions);

    // Call the callback function provided by the autocomplete library
    doneCallback({ suggestions: suggestions });
}

function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    window.location.href = 'movie-list.html?movie_query=' + suggestion["value"];
}

$(document).ready(function () {
    $('#movieQuery').autocomplete({
        lookup: function (query, doneCallback) {
            handleLookup(query, doneCallback);
        },
        onSelect: function (suggestion) {
            handleSelectSuggestion(suggestion);
        },
        deferRequestBy: 300,
        minChars: 3,
    });
});


function handleNormalSearch(query) {
    window.location.href = 'movie-list.html?movie_query=' + encodeURIComponent(query);
}

$('#full-text-search-form').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#full-text-search-form').val())
    }
})
