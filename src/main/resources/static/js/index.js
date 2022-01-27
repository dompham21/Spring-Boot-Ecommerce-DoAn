
$('.dropdown-profile').click(function(){
    $('.dropdown-menu-profile').toggleClass('show');
    $('.dropdown-filter').toggleClass('hide-opacity');
});

$('.dropdown-filter').click(function(){

    $('.dropdown-menu-filter').toggleClass('show');

});

function showDeleteConfirmModal(link, entityName) {
    entityId = link.attr("entityId");

    $("#yesButton").attr("href", link.attr("href"));
    $("#confirmText").text("Are you sure you want to delete this "
        + entityName + " ID " + entityId + "?");
    $("#confirmModal").modal();
}


function formatNumber(event, input) {
    if(event.which >= 37 && event.which <= 40){
        event.preventDefault();
    }
    input.val(function(index, value) {
        return value
            .replace(/\D/g, "")
            .replace(/([0-9])([0-9]{3})$/, '$1.$2')
            .replace(/\B(?=(\d{3})+(?!\d)\.?)/g, ".")
            .replace(/^0+(\d)/, '$1')
            ;
    });
}

/** double to price */
function formatPrice(inputPrice) {
    return parseFloat(inputPrice).toLocaleString('en-US', {minimumFractionDigits: 0, maximumFractionDigits: 0});
}


/**  yyyy-mm-dd to dd/mm/yyyy   */
function formatDate(inputDate) {
    let date = new Date(inputDate);
    if (!isNaN(date.getTime())) {
        // Months use 0 index.
        const day = String(date.getDate()).padStart(2, '0');

        return day + '/' + date.getMonth() + 1 + '/' + date.getFullYear();
    }
}


let loadFile = function(event) {
    let data = new FormData();
    data.append("file",event.target.files[0]);
    $.ajax ({
        data: data,
        type: "POST",
        url: "/file/upload",
        cache: false,
        enctype : 'multipart/form-data',
        contentType: false,
        processData: false,
        success: function(url) {
            let output = document.getElementById('image-output');
            $('#image-output').css('display', 'block')
            output.src = url;
            $('.upload-zone-content').css('display', 'none');
        },
        error: function(data) {
            console.log(data);
        }
    });

};

function uploadImage(image) {
    let data = new FormData();
    data.append("file",image);
    $.ajax ({
        data: data,
        type: "POST",
        url: "/file/upload",
        cache: false,
        enctype : 'multipart/form-data',
        contentType: false,
        processData: false,
        success: function(url) {
            $('#description').summernote("insertImage", url);
        },
        error: function(data) {
            console.log(data);
        }
    });
}

function clearFilter(entityName) {
    window.location = `/admin/${entityName}/page/1`;
}

function handleInputNumber(evt) {
    let max = parseInt(evt.currentTarget.getAttribute('max'));
    let number = parseInt(evt.target.value);

    if(number >= max) {
        evt.target.value = max;
    }
    else if(number <=0 || isNaN(number)) {
        evt.target.value = 1;
    }
    else {
        evt.target.value = number;
    }
}


function addToCart(productId) {
    let quantity = $("#quantity" + productId).val();
    let url = '/cart/add/'+ productId + '/' + quantity;
    $.ajax({
        type: "POST",
        url: url,
    })
    .done(function(response) {
        swal(
            '',
            response,
            'success'
        )
    })
    .fail(function (error) {

        if(error.status = 401) { //Unauthorized
            const wrapper = document.createElement('div');
            wrapper.innerHTML = "<div><div>" + error.responseText + "</div><div>Go to <a style='color: #4c78dd; font-weight: 700' href='/login'>Login Page</a></div></div>"
            swal( {
                title: '',
                icon: 'error',
                content: wrapper,
            })

        }
        else {
            swal(
                '',
                error.responseText,
                'error'
            )
        }
    })





}

function showErrorLoginPage() {
    const wrapper = document.createElement('div');
    wrapper.innerHTML = "<div><div>You must login to add this product to cart.</div><div>Go to <a style='color: #4c78dd; font-weight: 700' href='/login'>Login Page</a></div></div>"
    swal( {
        title: '',
        icon: 'error',
        content: wrapper,
    })
}

$(document).on('click', '.dropdown-menu', function (e) {
    e.stopPropagation();
});

function increaseQuantity(e, productId) {
    e.parentNode.querySelector('input[type=number]').stepUp();
    updatedQuantityCart(productId);
}

function decreaseQuantity(e, productId) {
    e.parentNode.querySelector('input[type=number]').stepDown();
    updatedQuantityCart(productId);
}

function updatedQuantityCart(productId) {
    quantity = $("#quantity" + productId).val();
    url =  "/cart/update/" + productId + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
    }).done(function(response) {
        if(response.status === "OK") {
            $("#subtotal" + productId).text(formatPrice(response.subtotal));
            $("#cart-estimatedTotal").text(formatPrice(response.estimatedTotal));
        }
    }).fail(function(error) {
        swal(
            '',
            error.responseText,
            'error'
        )
    });
}

function deleteItemCart(productId) {
    url = "/cart/delete/" + productId;
    $.ajax({
        type: "DELETE",
        url: url,
    }).done(function(response) {
        if(response.status === "OK") {
            $("#cart-estimatedTotal").text(formatPrice(response.estimatedTotal));
            removeItemFromHtml(productId);
            if($('.cart-item-count').length === 0) {
                $("#sectionEmptyCartMessage").removeClass("d-none");
                $("#sectionEmptyCartMessage").addClass("d-flex");
                $("#section-checkout-empty").removeClass("d-none");
                $("#section-checkout-empty").addClass("d-flex");
                $("#checkout-controller").remove();
                $(".nav-badge").remove();
                $(".pulse-ring").remove();
            }
        }
    }).fail(function(error) {
        swal(
            '',
            error.responseText,
            'error'
        )
    });
}

function removeItemFromHtml(productId) {
    $("#cartitem"+productId).remove();
}