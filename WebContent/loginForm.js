document
    .getElementById('login-form')
    .addEventListener('submit', function (event) {
        event.preventDefault() // Prevent form submission

        // Get form values
        const email = document.getElementById('email').value
        const password = document.getElementById('password').value

        // Validate form values
        // Send form values to server

        // jQuery.ajax({
        //     dataType: "json",  // Setting return data type
        //     method: "POST",// Setting request method
        //     url: "api/single-movie?email=" + email, // Setting request url, which is mapped by StarsServlet in Stars.java
        //     success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
        // });


        // Clear all form fields
        document.getElementById('email').value = ''
        document.getElementById('password').value = ''

        alert('')
    })