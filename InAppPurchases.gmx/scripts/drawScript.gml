deadzone = 0.25;

AXIS_LS_X = "0";
AXIS_LS_Y = "1";
AXIS_RS_X = "11";
AXIS_RS_Y = "14";
AXIS_L2 = "17";
AXIS_R2 = "18";
BUTTON_O = "96";
BUTTON_U = "99";
BUTTON_Y = "100";
BUTTON_A = "97";
BUTTON_L1 = "102";
BUTTON_R1 = "103";
BUTTON_L3 = "106";
BUTTON_R3 = "107";
BUTTON_DPAD_UP = "19";
BUTTON_DPAD_DOWN = "20";
BUTTON_DPAD_RIGHT = "22";
BUTTON_DPAD_LEFT = "21";
BUTTON_MENU = "82";

INDEX_SPRITE_ACTIVE = 0;
INDEX_SPRITE_INACTIVE = 1;

INDEX_REQUEST_LOGIN = 0;
INDEX_REQUEST_PRODUCTS = 1;
INDEX_REQUEST_PURCHASE = 2;
INDEX_REQUEST_RECEIPTS = 3;
INDEX_REQUEST_GAMERINFO = 4;
INDEX_EXIT = 5;

draw_text_colour(150, 100, "Hello from Game Maker!", c_white, c_white, c_white, c_white, 1);

draw_text_colour(175, 140, text_message, c_white, c_white, c_white, c_white, 1);

pressed = device_mouse_check_button(0, mb_left);
released = device_mouse_check_button_released(0, mb_left);
touchX = device_mouse_x(0);
touchY = device_mouse_y(0);

draw_text_colour(175, 200, "Pressed="+string(pressed)+" Position X="+string(touchX)+" Y="+string(touchY), c_white, c_white, c_white, c_white, 1);

button_is_active = false;

padding = 10;
x = 150;
y = 280;
scaleX = 1;
scaleY = 0.5;
width = 200;
height = 50
if (x < touchX && touchX < (x+width) && (y) < touchY && touchY < (y+height))
{
    spriteIndex = INDEX_SPRITE_ACTIVE;
    button_index = INDEX_REQUEST_LOGIN;
    button_is_active = true;
}
else
{
    spriteIndex = INDEX_SPRITE_INACTIVE;
}
draw_sprite_ext(spriteIndex, -1, x+25, y, scaleX, scaleY, 0, c_white, 1);
draw_text_colour(x, y, "Login", c_white, c_white, c_white, c_white, 1);

x += width + padding;
scaleX = 1.75;
scaleY = 0.5;
width = 300;
if (x < touchX && touchX < (x+width) && (y) < touchY && touchY < (y+height))
{
    spriteIndex = INDEX_SPRITE_ACTIVE;
    button_index = INDEX_REQUEST_PRODUCTS;
    button_is_active = true;
}
else
{
    spriteIndex = INDEX_SPRITE_INACTIVE;
}
draw_sprite_ext(spriteIndex, -1, x+75, y, scaleX, scaleY, 0, c_white, 1);
draw_text_colour(x, y, "Request Products", c_white, c_white, c_white, c_white, 1);

x += width + padding;
scaleX = 1.75;
width = 300;
if (x < touchX && touchX < (x+width) && (y) < touchY && touchY < (y+height))
{
    spriteIndex = INDEX_SPRITE_ACTIVE;
    button_index = INDEX_REQUEST_PURCHASE;
    button_is_active = true;
}
else
{
    spriteIndex = INDEX_SPRITE_INACTIVE;
}
draw_sprite_ext(spriteIndex, -1, x+75, y, scaleX, scaleY, 0, c_white, 1);
draw_text_colour(x, y, "Request Purchase", c_white, c_white, c_white, c_white, 1);

x += width + padding;
scaleX = 1.75;
width = 300;
if (x < touchX && touchX < (x+width) && (y) < touchY && touchY < (y+height))
{
    spriteIndex = INDEX_SPRITE_ACTIVE;
    button_index = INDEX_REQUEST_RECEIPTS;
    button_is_active = true;
}
else
{
    spriteIndex = INDEX_SPRITE_INACTIVE;
}
draw_sprite_ext(spriteIndex, -1, x+75, y, scaleX, scaleY, 0, c_white, 1);
draw_text_colour(x, y, "Request Receipts", c_white, c_white, c_white, c_white, 1);

x += width + padding;
scaleX = 1.75;
width = 300;
if (x < touchX && touchX < (x+width) && (y) < touchY && touchY < (y+height))
{
    spriteIndex = INDEX_SPRITE_ACTIVE;
    button_index = INDEX_REQUEST_GAMERINFO;
    button_is_active = true;
}
else
{
    spriteIndex = INDEX_SPRITE_INACTIVE;
}
draw_sprite_ext(spriteIndex, -1, x+75, y, scaleX, scaleY, 0, c_white, 1);
draw_text_colour(x, y, "Request GamerInfo", c_white, c_white, c_white, c_white, 1);

x += width + padding;
scaleX = 1;
width = 200;
if (x < touchX && touchX < (x+width) && (y) < touchY && touchY < (y+height))
{
    spriteIndex = INDEX_SPRITE_ACTIVE;
    button_index = INDEX_EXIT;
    button_is_active = true;
}
else
{
    spriteIndex = INDEX_SPRITE_INACTIVE;
}
draw_sprite_ext(spriteIndex, -1, x+25, y, scaleX, scaleY, 0, c_white, 1);
draw_text_colour(x, y, "Exit", c_white, c_white, c_white, c_white, 1);

controllers_connected = RazerSDK_IsAnyConnected();
if (controllers_active != controllers_connected)
{
    controllers_active = controllers_connected;
    if (controllers_active) {
        text_message = "Status: Controller detected!";
    } else {
        text_message = "Status: No controllers are connected!";
    }
}


if (RazerSDK_GetAnyButtonUp(BUTTON_DPAD_RIGHT))
{
    if (button_index < INDEX_EXIT)
    {
        button_index = button_index + 1;
    }
}

if (RazerSDK_GetAnyButtonUp(BUTTON_DPAD_LEFT))
{
    if (button_index > INDEX_REQUEST_PRODUCTS)
    {
        button_index = button_index - 1;
    }
}

if ((released && button_is_active) || RazerSDK_GetAnyButtonUp(BUTTON_O))
{
    if (button_index == INDEX_REQUEST_PRODUCTS)
    {
        text_message = "Status: Requesting products...";
        products_index = 0;
        products_length = 0;
        RazerSDK_RequestProducts(strPurchasables);
    }
    
    if (button_index == INDEX_REQUEST_PURCHASE)
    {
        text_message = "Status: Requesting purchase...";
        if (products_index < array_length_1d(product_ids) &&
            products_length > 0)
        {
            RazerSDK_RequestPurchase(product_ids[products_index], "ENTITLEMENT");
        }
        else
        {
            text_message = "Status: Request products to select a purchase...";
        }
    }    
    
    if (button_index == INDEX_REQUEST_RECEIPTS)
    {
        text_message = "Status: Requesting receipts...";
        receipts_length = 0;
        RazerSDK_RequestReceipts();
    }
    
    if (button_index == INDEX_REQUEST_LOGIN)
    {
        text_message = "Status: Requesting login...";
        RazerSDK_RequestLogin();
    }
    
    if (button_index == INDEX_REQUEST_GAMERINFO)
    {
        text_message = "Status: Requesting gamer info...";
        RazerSDK_RequestGamerInfo();
    }

    if (button_index == INDEX_EXIT)
    {
        text_message = "Status: Exiting game...";
        RazerSDK_Shutdown();
    }
}

if (button_index == INDEX_REQUEST_PRODUCTS ||
    button_index == INDEX_REQUEST_PURCHASE)
{
    if (RazerSDK_GetAnyButtonUp(BUTTON_DPAD_UP))
    {
        if (products_index > 0) {
            --products_index;
        }
    }
    if (RazerSDK_GetAnyButtonUp(BUTTON_DPAD_DOWN))
    {
        if ((products_index+1) < products_length) {
            ++products_index;
        }
    }
    x = 150;
    y = 400;
    for (index = 0; index < products_length; ++index)
    {
        width = 400;
        height = 50;
        if (x < touchX && touchX < (x+width) && (y) < touchY && touchY < (y+height))
        {
            products_index = index;
        }
        if (index == products_index) {
            draw_text_colour(x, y, "* "+products[index], c_white, c_white, c_white, c_white, 1);
        } else {
            draw_text_colour(x, y, products[index], c_white, c_white, c_white, c_white, 1);
        }
        y += 50;
    }
}

if (button_index == INDEX_REQUEST_RECEIPTS)
{
    x = 150;
    y = 400;
    for (index = 0; index < receipts_length; ++index)
    {
        draw_text_colour(x, y, receipts[index], c_white, c_white, c_white, c_white, 1);
        y += 25;
    }
}

asyncResult = RazerSDK_GetAsyncResult();
if (asyncResult != undefined &&
    asyncResult != "") {
    text_message = "Status: "+asyncResult;
    asyncMethod = RazerSDK_GetAsyncMethod();
    if (asyncMethod != undefined &&
        asyncMethod != "") {
        text_message = "Status: Method="+asyncMethod+" json="+asyncResult;
        if (asyncMethod == "onSuccessInit") {
            var deviceName = RazerSDK_GetDeviceHardwareName();
            var buttonName = RazerSDK_GetButtonName(BUTTON_O);
            text_message = "Init Success: DeviceName="+deviceName+" BUTTON_O name="+buttonName;
        }
        else if (asyncMethod == "onSuccessRequestLogin") {
            text_message = "Status: RequestLogin Success";
        }
        else if (asyncMethod == "onFailureRequestLogin") {
            var errorCode = RazerSDK_GetAsyncDataString("errorCode");
            var errorMessage = RazerSDK_GetAsyncDataString("errorMessage");            
            text_message = "Status: RequestLogin errorCode="+errorCode+" errorMessage="+errorMessage;
        }
        else if (asyncMethod == "onCancelRequestLogin") {
            text_message = "Status: RequestLogin Cancel";
        }
        else if (asyncMethod == "onSuccessRequestGamerInfo") {
            var uuid = RazerSDK_GetAsyncDataString("uuid");
            var username = RazerSDK_GetAsyncDataString("username");            
            text_message = "Status: RequestGamerInfo uuid="+uuid+" username="+username;
        }
        else if (asyncMethod == "onFailureRequestGamerInfo") {
            var errorCode = RazerSDK_GetAsyncDataString("errorCode");
            var errorMessage = RazerSDK_GetAsyncDataString("errorMessage");            
            text_message = "Status: RequestGamerInfo errorCode="+errorCode+" errorMessage="+errorMessage;
        }
        else if (asyncMethod == "onCancelRequestGamerInfo") {
            text_message = "Status: RequestGamerInfo Cancel";
        }
        else if (asyncMethod == "onSuccessRequestProducts") {
            var count = RazerSDK_GetAsyncDataArrayCount();
            products_length = count;
            text_message = "Status: RequestProducts count="+string(count);
            if (count > 0) {
                text_message = "Status: RequestProducts count="+string(count);
            }
            for (var index = 0; index < products_length; ++index)
            {
                var identifier = RazerSDK_GetAsyncDataArrayString(string(index), "identifier");
                var localPrice = RazerSDK_GetAsyncDataArrayDouble(string(index), "localPrice");
                products[index] = "Product identifier="+identifier+" localPrice="+string(localPrice);
                product_ids[index] = identifier;
            }
        }
        else if (asyncMethod == "onFailureRequestProducts") {
            var errorCode = RazerSDK_GetAsyncDataString("errorCode");
            var errorMessage = RazerSDK_GetAsyncDataString("errorMessage");            
            text_message = "Status: RequestProducts errorCode="+errorCode+" errorMessage="+errorMessage;
        }
        else if (asyncMethod == "onSuccessRequestPurchase") {
            var identifier = RazerSDK_GetAsyncDataString("identifier");
            text_message = "Status: RequestPurchase identifier="+identifier;
        }
        else if (asyncMethod == "onFailureRequestPurchase") {
            var errorCode = RazerSDK_GetAsyncDataString("errorCode");
            var errorMessage = RazerSDK_GetAsyncDataString("errorMessage");            
            text_message = "Status: RequestPurchase errorCode="+errorCode+" errorMessage="+errorMessage;
        }
        else if (asyncMethod == "onCancelRequestPurchase") {
            text_message = "Status: RequestPurchase cancelled!";
        }
        else if (asyncMethod == "onSuccessRequestReceipts") {
            var count = RazerSDK_GetAsyncDataArrayCount();
            receipts_length = count;
            text_message = "Status: RequestReceipts count="+string(count);
            if (count > 0) {
                text_message = "Status: RequestReceipts count="+string(count);
            }
            for (var index = 0; index < receipts_length; ++index)
            {
                var identifier = RazerSDK_GetAsyncDataArrayString(string(index), "identifier");
                var localPrice = RazerSDK_GetAsyncDataArrayDouble(string(index), "localPrice");
                receipts[index] = "Receipt identifier="+identifier+" localPrice="+string(localPrice);
            }
        }
        else if (asyncMethod == "onFailureRequestReceipts") {
            var errorCode = RazerSDK_GetAsyncDataString("errorCode");
            var errorMessage = RazerSDK_GetAsyncDataString("errorMessage");            
            text_message = "Status: RequestReceipts errorCode="+errorCode+" errorMessage="+errorMessage;
        }
        else if (asyncMethod == "onCancelRequestReceipts") {
            text_message = "Status: RequestReceipts cancelled!";
        }
        else if (asyncMethod == "onSuccessShutdown") {
            text_message = "Status: Shutdown success!";
            game_end();
        }
        else if (asyncMethod == "onFailureShutdown") {
            text_message = "Status: Shutdown failure!";
        }
    }
    RazerSDK_PopAsyncResult();
}

