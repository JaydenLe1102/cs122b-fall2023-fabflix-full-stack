$(document).ready(function () {
    // Retrieve the salesData parameter from the URL
    const params = new URLSearchParams(window.location.search);
    const salesDataString = params.get('salesData');

    // Parse the salesData JSON string into an object
    const salesDataObject = JSON.parse(decodeURIComponent(salesDataString));

    // Convert the object into an array using Object.values()
    const salesData = Object.values(salesDataObject);

    // Verify the data structure to ensure it's an array of objects
    console.log(salesData);

    // Populate sales table with data
    salesData.forEach(sale => {
        const price = 100; // Default price per item
        const total = price * sale.quantity;

        $('#sales-table-body').append(`
            <tr>
                <td>${sale.saleId}</td>
                <td>${sale.movieTitle}</td>
                <td>${sale.quantity}</td>
                <td>$${price.toFixed(2)}</td>
                <td>$${total.toFixed(2)}</td>
            </tr>
        `);
    });

    // Calculate grand total
    const grandTotal = salesData.reduce((acc, sale) => acc + (sale.quantity * 100), 0);

    // Display grand total
    $('#grand-total').text(`$${grandTotal.toFixed(2)}`);
});