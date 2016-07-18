purchasables[0] = "long_sword";
purchasables[1] = "sharp_axe";
purchasables[2] = "cool_level";
purchasables[3] = "awesome_sauce";
purchasables[4] = "__DECLINED__THIS_PURCHASE";

strPurchasables = purchasables[0];
for (index = 1; index < array_length_1d(purchasables); ++index)
{
    strPurchasables += ","+purchasables[index];
}

var SECRET_API_KEY = "eyJkZXZlbG9wZXJfaWQiOiIzMTBhOGY1MS00ZDZlLTRhZTUtYmRhMC1iOTM4NzhlNWY1ZDAiLCJkZXZlbG9wZXJfcHVibGljX2tleSI6Ik1JR2ZNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0R05BRENCaVFLQmdRRFhNdlZHS2dudUhBVndMclVBd2g5Vzk3M1lzQ0lPRTBrRFRDcTlvWkNqbWJUMjhOVlIyK1MwbzVsbnZWL2h3ZEk0ZlcvemxzbE41Uk1zRnV6NnJ1TkFoUktUVkhyc2VzcDJHc25yOFJvVTlqMFJJNURvL2VKbWM4akc3Nm51NUJ2YXJmQWV5em41Z3VJaUFZbEY1b2V2ZWphQldnMTBGR3g5Y01Jb21XQnljUUlEQVFBQiJ9";
RazerSDK_Init(SECRET_API_KEY);

button_index = 0;
text_message = "Status:";
controllers_active = "";

products_index = 0;
products_length = 0;
products[0] = "";
product_ids[0] = "";

receipts_length = 0;
receipts[0] = "";
