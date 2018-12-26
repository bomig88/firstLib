package bomi.bomilib.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class CommonUtil {
    private static final String TAG = CommonUtil.class.getSimpleName();
    //---- String Pattern ----
    public static final String EMAIL_PATTERN = "^([\\w-]+(?:\\.[\\w-]+)*)@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([a-z]{2,6}(?:\\.[a-z]{2})?)$";
    //최소 1개의 대소문자, 숫자, 특수문자 조합, 최소 길이 6, 최대 길이 16
    public static final String PASSWORD_PATTERN = "^.*(?=^.{6,16}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$";
    public static final String TEL_NUMBER_PATTERN = "^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";
    public static final String TEL_HYPHEN_PATTERN = "(\\d{3})(\\d{3,4})(\\d{4})";
    /**
     * Formatter
     */
    private static DecimalFormat formatComma = new DecimalFormat("#,###,###"); //9,999,999
    public static SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    public static SimpleDateFormat formatYYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static SimpleDateFormat formatYYYY_MM_DD_hh_mm = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static SimpleDateFormat formatYYYY_DOT_MM_DOT_DD = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
    public static SimpleDateFormat formatYYYYMMDDKorean = new SimpleDateFormat("yyyy년MM월dd일", Locale.getDefault());
    public static SimpleDateFormat formatYYYYMMKorean = new SimpleDateFormat("yyyy년MM월", Locale.getDefault());
    public static SimpleDateFormat formatYYMMDD = new SimpleDateFormat("yyMMdd", Locale.getDefault());
    public static SimpleDateFormat formatYYYY = new SimpleDateFormat("yyyy", Locale.getDefault());
    public static SimpleDateFormat formatYYYYMM = new SimpleDateFormat("yyyyMM", Locale.getDefault());
    public static SimpleDateFormat formatMM = new SimpleDateFormat("MM", Locale.getDefault());
    public static SimpleDateFormat formatYYYYMMDDHHmmSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @SuppressWarnings("deprecation")
    public static Spanned getHtmlTagString(String htmlTagString) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Html.fromHtml(htmlTagString);
        } else {
            return Html.fromHtml(htmlTagString, Html.FROM_HTML_MODE_LEGACY);
        }
    }

    public static int getScreenWidthOrHeight(boolean isWidth, @NonNull Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if (isWidth)
            return size.x;
        else
            return size.y;
    }

    /**
     * 소프트 키패드 숨기기
     *
     * @param context 현재 노출 중인 context / Activity
     */
    public static void hideKeyboard(Activity context) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = context.getCurrentFocus();

            if (view == null)
                view = new View(context);

            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 키패드 'Enter / Done' 감지
     *
     * @param input 인풋뷰
     */
    public static void setEnterHideKeyboard(@NonNull EditText input) {
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_NEXT:
                    case EditorInfo.IME_ACTION_DONE:
                        return false;
                }

                return true;
            }
        });
    }

    /**
     * DP to Pixel
     *
     * @param context 컨텍스트
     * @param dp      변환할 dp 값
     * @return pixel value
     */
    public static int convertToPixels(Context context, float dp) {
        if (context == null)
            return (int) dp;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    /**
     * 천단위 , 로 포맷 변환
     *
     * @param value 숫자값이나 숫자값 String
     * @return #, ###
     */
    public static String getCommaDecimal(Object value) {
        return formatComma.format(value);
    }

    public static String removeLastChar(String str) {
        if (str == null || str.length() == 0)
            return str;
        else
            return str.substring(0, str.length() - 1);
    }

    /**
     * 전화번호 형식 확인
     *
     * @param cellphoneNumber 확인할 전화번호 값
     * @return Pattern Matched
     */
    public static boolean isValidProblemTelNumber(String cellphoneNumber) {
        Pattern p = Pattern.compile(TEL_NUMBER_PATTERN);
        Matcher matcher = p.matcher(cellphoneNumber);
        return !matcher.matches();
    }

    /**
     * 주민등록번호 뒷자리를 암호문자로 변환
     *
     * @param value 변환할 주민등록번호 뒷자리 값
     * @return 주민등록번호 뒷자리 숫자 개수와 같은 길이의 * 문자열
     */
    public static String hiddenSSNLast(@NonNull String value) {
        StringBuilder hidden = new StringBuilder();

        for (int l = 0; l < value.length(); l++)
            hidden.append("*");

        return hidden.toString();
    }

    /**
     * 전화번호 숫자를 - 적용
     *
     * @param phoneNumber 전화번호
     * @return 000-0000-0000 or 000-000-0000
     */
    public static String makePhoneNumber(String phoneNumber) {
        if (!Pattern.matches(TEL_HYPHEN_PATTERN, phoneNumber)) return null;
        return phoneNumber.replaceAll(TEL_HYPHEN_PATTERN, "$1-$2-$3");
    }

    /**
     * market Version 형식 맞추기
     *
     * @param lengthBigger 길이가 더 긴 버전
     * @param changer      길이가 더 짧은 버전
     * @return 변경된 버전값 반환 (31, 228) 이 들어오면 310이 돌아감
     */
    public static String matchVersionLengthBigger(String lengthBigger, String changer) {
        StringBuilder val = new StringBuilder(changer);

        for (int idx = changer.length(); idx < lengthBigger.length(); idx++) {
            val.append("0");
        }

        return val.toString();
    }

    /**
     * 0~9의 숫자값을 랜덤한 순서로 생성 후 left, right 배치
     * <p>
     * [    ] [    ] [    ]
     * [    ] [    ] [    ]
     * [    ] [    ] [    ]
     * [left] [    ] [right]
     *
     * @param left 하단 좌측에 나타낼 항목값
     * @return 생성된 배열
     */
    public static String[] createRandomPad(@NonNull String left) {
        ArrayList<String> defPad = new ArrayList<>(new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")));

        Collections.shuffle(defPad);
        ArrayList<String> arrPad = new ArrayList<>(defPad);

        arrPad.add(arrPad.size() - 1, left);

        return arrPad.toArray(new String[arrPad.size()]);
    }

    public static String[] createRandomPad() {
        ArrayList<String> defPad = new ArrayList<>(new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")));

        Collections.shuffle(defPad);
        ArrayList<String> arrPad = new ArrayList<>(defPad);

        return arrPad.toArray(new String[arrPad.size()]);
    }

    /**
     * 날짜 형식 문자열인지 확인
     *
     * @param date   문자열
     * @param format 기준 날짜 포맷
     * @return Format Pattern Matched
     */
    public static boolean isValidDatePattern(@NonNull String date, @NonNull SimpleDateFormat format) {
        try {
            Date parseDate = format.parse(date);

            return (parseDate != null);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidBirthDayPatternYYYYMMDD(@NonNull String date) {
        try {
            Date parseDate = formatYYYYMMDD.parse(date);

            if (parseDate != null) {
                String year = date.substring(0, 4);
                String month = date.substring(4, 6);
                String day = date.substring(6, 8);

                Calendar c = Calendar.getInstance();
                c.setTime(parseDate);

                if (Integer.parseInt(year) == c.get(Calendar.YEAR)
                        && Integer.parseInt(month) == (c.get(Calendar.MONTH) + 1)
                        && Integer.parseInt(day) == c.get(Calendar.DATE)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "isValidBirthDayPatternYYYYMMDD :: ", e);
        }
        return false;
    }

    public static String changeDataPattern(@NonNull String date, @NonNull SimpleDateFormat defFormat, @NonNull SimpleDateFormat changeFormat) {
        Date defDate = getDateFromPattern(date, defFormat);
        if (defDate != null)
            return getPatternFromDate(defDate, changeFormat);
        else
            return "";
    }

    public static Date getDateFromPattern(@NonNull String date, @NonNull SimpleDateFormat format) {
        try {
            return format.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getPatternFromDate(@NonNull Date date, @NonNull SimpleDateFormat format) {
        return format.format(date);
    }

    /**
     * 나이 계산
     *
     * @param birthDate 생년월일
     * @param format    생년월일 포맷 형식
     * @return Age
     */
    public static int calculateAge(@NonNull String birthDate, @NonNull SimpleDateFormat format) {
        try {
            Calendar today = Calendar.getInstance();
            Calendar birthday = Calendar.getInstance();

            birthday.setTime(getDateFromPattern(birthDate, format));
            return (today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR));
        } catch (Exception e) {
            Log.e(TAG, "calculateAge", e);
        }

        return 0;
    }

    /**
     * 기준일 범위 체크
     *
     * @param expireDate 기준일자
     * @param checkDate  범위값 (기준일자로부터 10일 이후까지 범위지정이면 10 입력)
     * @param format     날짜 포맷
     * @return true / false
     */
    public static boolean isValidInDate(String expireDate, int checkDate, @NonNull SimpleDateFormat format) {
        try {
            Date date = format.parse(expireDate);
            Calendar calcuDefDate = Calendar.getInstance();
            calcuDefDate.setTime(date);

            Calendar calcu30Date = Calendar.getInstance();
            calcu30Date.setTime(date);
            calcu30Date.add(Calendar.DATE, +checkDate);
            String maxExpiredFormatter = format.format(calcu30Date.getTime());

            Calendar calcuToday = Calendar.getInstance();
            String todayFormatter = format.format(calcuToday.getTime());

            if ((calcuToday.after(calcuDefDate) || expireDate.equals(todayFormatter))
                    && (calcuToday.before(calcu30Date) || maxExpiredFormatter.equals(todayFormatter)))
                return true;

        } catch (Exception e) {
            Log.e(TAG, "isValidInDate :: ", e);
        }

        return false;
    }

    /**
     * 미성년자 여부 체크
     *
     * @param birthDate 생년월일
     * @param format    생년월일 포맷 형식
     * @return true / false
     */
    public static boolean isAgeMinor(@NonNull String birthDate, @NonNull SimpleDateFormat format) {
        return isAgeYoungerDef(birthDate, format, 19);
    }

    /**
     * 기준값보다 나이가 어린지 확인
     *
     * @param birthDate 생년월일
     * @param format    생년월일 포맷 형식
     * @param defAge    기준 나이
     * @return true / false
     */
    public static boolean isAgeYoungerDef(@NonNull String birthDate, @NonNull SimpleDateFormat format, int defAge) {
        return (calculateAge(birthDate, format) < defAge);
    }

    public static boolean isBeforeToday(@NonNull String birthDate, @NonNull SimpleDateFormat format) {
        Calendar userBirthday = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        userBirthday.setTime(getDateFromPattern(birthDate, format));

        return (userBirthday.before(today));
    }

    /**
     * 사용자보다 생일이 어린지 체크
     *
     * @param userBirthDay    사용자 생일
     * @param youngerBirthDay 비교 대상 생일
     * @param format          생일 DateFormat 형식
     * @return true - 사용자보다 어림
     */
    public static boolean isYoungBirthThanMe(@NonNull String userBirthDay, @NonNull String youngerBirthDay, @NonNull SimpleDateFormat format) {
        try {
            Calendar userBirth = Calendar.getInstance();
            Calendar youngerBirth = Calendar.getInstance();

            userBirth.setTime(getDateFromPattern(userBirthDay, format));
            youngerBirth.setTime(getDateFromPattern(youngerBirthDay, format));

            return (userBirth.before(youngerBirth));
        } catch (Exception e) {
            Log.e(TAG, "isAfterBirthThanMe", e);
        }

        return false;
    }

    public static String parseChangeDate(SimpleDateFormat inputFormat, SimpleDateFormat outputFormat, String time) {
        String str = "";

        try {
            Date date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 버전 체크 후 업데이트 버전 있을 시 구글 마켓 오픈
     */
    public static void openGoogleMarket(@NonNull Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + activity.getPackageName()))
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (android.content.ActivityNotFoundException err) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName()))
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    /**
     * 버전 체크 후 업데이트 버전 있을 시 구글 마켓 오픈
     */
    public static void openOneStoreMarket(@NonNull Activity activity,@NonNull  String onsStoreId) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("onestore://common/product/"  + onsStoreId))
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (android.content.ActivityNotFoundException err) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://m.onestore.co.kr/mobilepoc/apps/appsDetail.omp?prodId=" + onsStoreId))
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    /**
     * Intent 종류에 해당하는 앱이 있는지 확인
     *
     * @param activity 액티비티
     * @param intent   인텐트
     * @return 해당 앱 있음 여부
     */
    private static boolean checkIntentApp(@NonNull Activity activity, Intent intent) {
        PackageManager packageManager = activity.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }

    public static boolean parseSchemeForOutBrowser(Activity activity, @NonNull Uri uri) {
        String scheme = uri.getScheme();
        if (scheme != null && activity != null) {
            switch (scheme) {
                case "tel":
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri); //콜 다이얼 표시

                    if (CommonUtil.checkIntentApp(activity, intent))
                        activity.startActivity(intent);

                    return false;

                case "sms":
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.putExtra("address", uri);
                    intent.setType("vnd.android-dir/mms-sms");

                    if (CommonUtil.checkIntentApp(activity, intent))
                        activity.startActivity(intent);
                    return false;

                case "market":
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return false;

                case "intent":
                    try {
                        intent = Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME);

                        if (intent != null && intent.getPackage() != null) {
                            Intent existPackage = activity.getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                            if (existPackage != null) {
                                activity.startActivity(intent);
                            } else {
                                Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                                marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                                activity.startActivity(marketIntent);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "parseSchemeForOutBrowser :: ", e);
                    }

                    return false;
            }
        }
        return true;
    }

    /**
     * Web Editor Content Screen Fit Size Header
     *
     * @param content HTML
     * @return content with header
     */
    public static String getFitScreenHtml(@NonNull String content) {
        return "<html><head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0\">" +
                "<style>img{display: inline;height: auto;max-width: 100%;}</style>" +
                "</head><body>" +
                content +
                "</body></html>";
    }

    private static ObjectAnimator getAnimSelfRotation(ImageView view) {
        ObjectAnimator animRotate = ObjectAnimator.ofFloat(view, "rotation", 0, 360);
        animRotate.setDuration(500);
        animRotate.setInterpolator(new LinearInterpolator());
        animRotate.setRepeatCount(ObjectAnimator.INFINITE);

        return animRotate;
    }

    public static int getPercent(float def, float cur) {
        float percent = ((def - cur) / def) * 100f;

        return (int) percent;
    }

    public static <T> int getListSize(ArrayList<T> arrayList) {
        return (arrayList == null ? 0 : arrayList.size());
    }
}
