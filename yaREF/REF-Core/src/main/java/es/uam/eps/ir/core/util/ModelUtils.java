package es.uam.eps.ir.core.util;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Alejandro
 */
public class ModelUtils<U, I, C extends ContextIF> {

    private Map<U, Double> mapMinUserRating;
    private Map<U, Double> mapMaxUserRating;
    private Map<I, Double> mapMinItemRating;
    private Map<I, Double> mapMaxItemRating;
    private Map<I, Map<Float, Double>> mapItemRatingByStdNorm;
    private Map<U, Map<Float, Double>> mapUserRatingByStdNorm;
    private Map<U, Double> mapAvgUserRating;
    private Map<I, Double> mapAvgItemRating;
    private Map<Integer, Double> mapTotalRatings;
    private static final int NUMBER_RATINGS = 0;
    private static final int TOTAL_RATING = 1;
    private static final int AVG_RATING = 2;
    private static final int TOTAL_RATING_USER_NORM = 3;
    private static final int TOTAL_RATING_ITEM_NORM = 4;
    private Map<Double, int[]> mapTotalThresholdRatings;
    protected ModelIF<U, I, C> model;

    public ModelUtils(final ModelIF<U, I, C> model) {
        this.model = model;
        mapMinUserRating = new HashMap<U, Double>();
        mapMaxUserRating = new HashMap<U, Double>();
        mapMinItemRating = new HashMap<I, Double>();
        mapMaxItemRating = new HashMap<I, Double>();
        mapItemRatingByStdNorm = new HashMap<I, Map<Float, Double>>();
        mapUserRatingByStdNorm = new HashMap<U, Map<Float, Double>>();
        mapAvgUserRating = new HashMap<U, Double>();
        mapAvgItemRating = new HashMap<I, Double>();
        mapTotalRatings = new HashMap<Integer, Double>();
        mapTotalThresholdRatings = new HashMap<Double, int[]>();
    }

    public double getRatingByStandardItemNormalisation(final I item, final PreferenceIF pref) {
        return getRatingByStandardItemNormalisation(item, pref, model, mapItemRatingByStdNorm, mapMaxItemRating, mapMinItemRating);
    }

    public static <U, I, C extends ContextIF> double getRatingByStandardItemNormalisation(final I item, final PreferenceIF pref, final ModelIF<U, I, C> model, final Map<I, Map<Float, Double>> mapItemRatingByStdNorm, final Map<I, Double> mapMaxItemRating, final Map<I, Double> mapMinItemRating) {
        double r = Double.NaN;
        if (mapItemRatingByStdNorm != null && mapItemRatingByStdNorm.containsKey(item) && mapItemRatingByStdNorm.get(item).containsKey(pref.getValue())) {
            r = mapItemRatingByStdNorm.get(item).get(pref.getValue());
        } else {
            double min = getMinItemRating(item, model, mapMinItemRating);
            double max = getMaxItemRating(item, model, mapMaxItemRating);
            r = (pref.getValue() - min) / (max - min);
            if (Double.isNaN(r) || Double.isInfinite(r)) {
                r = 0.0;
            }
            if (mapItemRatingByStdNorm != null) {
                Map<Float, Double> m = mapItemRatingByStdNorm.get(item);
                if (m == null) {
                    m = new HashMap<Float, Double>();
                    mapItemRatingByStdNorm.put(item, m);
                }
                m.put(pref.getValue(), r);
            }
        }
        return r;
    }

    public double getRatingByStandardUserNormalisation(final U user, final PreferenceIF pref) {
        return getRatingByStandardUserNormalisation(user, pref, model, mapUserRatingByStdNorm, mapMaxUserRating, mapMinUserRating);
    }

    public static <U, I, C extends ContextIF> double getRatingByStandardUserNormalisation(final U user, final PreferenceIF pref, final ModelIF<U, I, C> model, final Map<U, Map<Float, Double>> mapUserRatingByStdNorm, final Map<U, Double> mapMaxUserRating, final Map<U, Double> mapMinUserRating) {
        double r = Double.NaN;
        if (mapUserRatingByStdNorm != null && mapUserRatingByStdNorm.containsKey(user) && mapUserRatingByStdNorm.get(user).containsKey(pref.getValue())) {
            r = mapUserRatingByStdNorm.get(user).get(pref.getValue());
        } else {
            double min = getMinUserRating(user, model, mapMinUserRating);
            double max = getMaxUserRating(user, model, mapMaxUserRating);
            r = (pref.getValue() - min) / (max - min);
            if (Double.isNaN(r) || Double.isInfinite(r)) {
                r = 0.0;
            }
            if (mapUserRatingByStdNorm != null) {
                Map<Float, Double> m = mapUserRatingByStdNorm.get(user);
                if (m == null) {
                    m = new HashMap<Float, Double>();
                    mapUserRatingByStdNorm.put(user, m);
                }
                m.put(pref.getValue(), r);
            }
        }
        return r;
    }

    public double getAvgUserRating(final U u) {
        return getAvgUserRating(u, model, mapAvgUserRating);
    }

    public static <U, I, C extends ContextIF> double getAvgUserRating(final U u, final ModelIF<U, I, C> model, final Map<U, Double> mapAvgUserRating) {
        double r = 0.0;
        if (mapAvgUserRating != null && mapAvgUserRating.containsKey(u)) {
            r = mapAvgUserRating.get(u);
        } else {
            int n = 0;
            for (PreferenceIF<U, I, ?> p : model.getPreferencesFromUser(u)) {
                r += p.getValue();
                n++;
            }
            r = n == 0 ? 0.0 : r / n;
            if (mapAvgUserRating != null) {
                mapAvgUserRating.put(u, r);
            }
        }
        return r;
    }

    public double getMinUserRating(final U u) {
        return getMinUserRating(u, model, mapMinUserRating);
    }

    public static <U, I, C extends ContextIF> double getMinUserRating(final U u, final ModelIF<U, I, C> model, final Map<U, Double> mapMinUserRating) {
        double r = Double.MAX_VALUE;
        if (mapMinUserRating != null && mapMinUserRating.containsKey(u)) {
            r = mapMinUserRating.get(u);
        } else {
            for (PreferenceIF<U, I, ?> p : model.getPreferencesFromUser(u)) {
                if (p.getValue() < r) {
                    r = p.getValue();
                }
            }
            if (r == Double.MAX_VALUE) {
                r = 0.0;
            }
            if (mapMinUserRating != null) {
                mapMinUserRating.put(u, r);
            }
        }
        return r;
    }

    public double getMaxUserRating(final U u) {
        return getMaxUserRating(u, model, mapMaxUserRating);
    }

    public static <U, I, C extends ContextIF> double getMaxUserRating(final U u, final ModelIF<U, I, C> model, final Map<U, Double> mapMaxUserRating) {
        double r = Double.MIN_VALUE;
        if (mapMaxUserRating != null && mapMaxUserRating.containsKey(u)) {
            r = mapMaxUserRating.get(u);
        } else {
            for (PreferenceIF<U, I, C> p : model.getPreferencesFromUser(u)) {
                if (p.getValue() > r) {
                    r = p.getValue();
                }
            }
            if (r == Double.MIN_VALUE) {
                r = 0.0;
            }
            if (mapMaxUserRating != null) {
                mapMaxUserRating.put(u, r);
            }
        }
        return r;
    }

    public double getAvgItemRating(final I i) {
        return getAvgItemRating(i, model, mapAvgItemRating);
    }

    public static <U, I, C extends ContextIF> double getAvgItemRating(final I i, final ModelIF<U, I, C> model, final Map<I, Double> mapAvgItemRating) {
        double r = 0.0;
        if (mapAvgItemRating != null && mapAvgItemRating.containsKey(i)) {
            r = mapAvgItemRating.get(i);
        } else {
            int n = 0;
            for (PreferenceIF<U, I, ?> p : model.getPreferencesFromItem(i)) {
                r += p.getValue();
                n++;
            }
            r = n == 0 ? 0.0 : r / n;
            if (mapAvgItemRating != null) {
                mapAvgItemRating.put(i, r);
            }
        }
        return r;
    }

    public double getMinItemRating(final I i) {
        return getMinItemRating(i, model, mapMinItemRating);
    }

    public static <U, I, C extends ContextIF> double getMinItemRating(final I i, final ModelIF<U, I, C> model, final Map<I, Double> mapMinItemRating) {
        double r = Double.MAX_VALUE;
        if (mapMinItemRating != null && mapMinItemRating.containsKey(i)) {
            r = mapMinItemRating.get(i);
        } else {
            for (PreferenceIF<U, I, ?> p : model.getPreferencesFromItem(i)) {
                if (p.getValue() < r) {
                    r = p.getValue();
                }
            }
            if (r == Double.MAX_VALUE) {
                r = 0.0;
            }
            if (mapMinItemRating != null) {
                mapMinItemRating.put(i, r);
            }
        }
        return r;
    }

    public double getMaxItemRating(final I i) {
        return getMaxItemRating(i, model, mapMaxItemRating);
    }

    public static <U, I, C extends ContextIF> double getMaxItemRating(final I i, final ModelIF<U, I, C> model, final Map<I, Double> mapMaxItemRating) {
        double r = Double.MIN_VALUE;
        if (mapMaxItemRating != null && mapMaxItemRating.containsKey(i)) {
            r = mapMaxItemRating.get(i);
        } else {
            for (PreferenceIF<U, I, ?> p : model.getPreferencesFromItem(i)) {
                if (p.getValue() > r) {
                    r = p.getValue();
                }
            }
            if (r == Double.MIN_VALUE) {
                r = 0.0;
            }
            if (mapMaxItemRating != null) {
                mapMaxItemRating.put(i, r);
            }
        }
        return r;
    }

    public int getTotalRatings() {
        return getTotalRatings(model, mapTotalRatings);
    }

    public static <U, I, C extends ContextIF> int getTotalRatings(final ModelIF<U, I, C> model, final Map<Integer, Double> mapTotalRatings) {
        int r = 0;
        if (mapTotalRatings != null && mapTotalRatings.containsKey(TOTAL_RATING)) {
            r = mapTotalRatings.get(TOTAL_RATING).intValue();
        } else {
            for (U u : model.getUsers()) {
                for (PreferenceIF<U, I, ?> p : model.getPreferencesFromUser(u)) {
                    r += p.getValue();
                }
            }
            if (mapTotalRatings != null) {
                mapTotalRatings.put(TOTAL_RATING, 1.0 * r);
            }
        }
        return r;
    }

    public double getAverageRating() {
        return getAverageRating(model, mapTotalRatings);
    }

    public static <U, I, C extends ContextIF> double getAverageRating(final ModelIF<U, I, C> model, final Map<Integer, Double> mapTotalRatings) {
        double r = 0.0;
        if (mapTotalRatings != null && mapTotalRatings.containsKey(AVG_RATING)) {
            r = mapTotalRatings.get(AVG_RATING);
        } else {
            int n = 0;
            for (U u : model.getUsers()) {
                for (PreferenceIF<U, I, ?> p : model.getPreferencesFromUser(u)) {
                    r += p.getValue();
                    n++;
                }
            }
            r = n == 0 ? 0.0 : r / n;
            if (mapTotalRatings != null) {
                mapTotalRatings.put(AVG_RATING, r);
            }
        }
        return r;
    }

    public int getTotalRatingsUsingStandardItemNormalisation() {
        return getTotalRatingsUsingStandardItemNormalisation(model, mapTotalRatings, mapItemRatingByStdNorm, mapMaxItemRating, mapMinItemRating);
    }

    public static <U, I, C extends ContextIF> int getTotalRatingsUsingStandardItemNormalisation(final ModelIF<U, I, C> model, final Map<Integer, Double> mapTotalRatings,
            final Map<I, Map<Float, Double>> mapItemRatingByStdNorm, final Map<I, Double> mapMaxItemRating, final Map<I, Double> mapMinItemRating) {
        int r = 0;
        if (mapTotalRatings != null && mapTotalRatings.containsKey(TOTAL_RATING_ITEM_NORM)) {
            r = mapTotalRatings.get(TOTAL_RATING_ITEM_NORM).intValue();
        } else {
            for (U u : model.getUsers()) {
                for (PreferenceIF<U, I, C> p : model.getPreferencesFromUser(u)) {
                    r += getRatingByStandardItemNormalisation(p.getItem(), p, model, mapItemRatingByStdNorm, mapMaxItemRating, mapMinItemRating);
                }
            }
            if (mapTotalRatings != null) {
                mapTotalRatings.put(TOTAL_RATING_ITEM_NORM, 1.0 * r);
            }
        }
        return r;
    }

    public int getTotalRatingsUsingStandardUserNormalisation() {
        return getTotalRatingsUsingStandardUserNormalisation(model, mapTotalRatings, mapUserRatingByStdNorm, mapMaxUserRating, mapMinUserRating);
    }

    public static <U, I, C extends ContextIF> int getTotalRatingsUsingStandardUserNormalisation(final ModelIF<U, I, C> model, final Map<Integer, Double> mapTotalRatings,
            final Map<U, Map<Float, Double>> mapUserRatingByStdNorm, final Map<U, Double> mapMaxUserRating, final Map<U, Double> mapMinUserRating) {
        int r = 0;
        if (mapTotalRatings != null && mapTotalRatings.containsKey(TOTAL_RATING_USER_NORM)) {
            r = mapTotalRatings.get(TOTAL_RATING_USER_NORM).intValue();
        } else {
            for (U u : model.getUsers()) {
                for (PreferenceIF<U, I, C> p : model.getPreferencesFromUser(u)) {
                    r += getRatingByStandardUserNormalisation(u, p, model, mapUserRatingByStdNorm, mapMaxUserRating, mapMinUserRating);
                }
            }
            if (mapTotalRatings != null) {
                mapTotalRatings.put(TOTAL_RATING_USER_NORM, 1.0 * r);
            }
        }
        return r;
    }

    public int[] getNumberRatingsWithThreshold(final double ratingThreshold) {
        return getNumberRatingsWithThreshold(model, ratingThreshold, mapTotalThresholdRatings);
    }

    public static <U, I, C extends ContextIF> int[] getNumberRatingsWithThreshold(final ModelIF<U, I, C> model, final double ratingThreshold, final Map<Double, int[]> mapTotalThresholdRatings) {
        int[] r = new int[2];
        if (mapTotalThresholdRatings.containsKey(ratingThreshold)) {
            r = mapTotalThresholdRatings.get(ratingThreshold);
        } else {
            r[0] = 0;
            r[1] = 0;
            // r[0] == R>r
            for (U u : model.getUsers()) {
                for (PreferenceIF<U, I, ?> p : model.getPreferencesFromUser(u)) {
                    if (p.getValue() > ratingThreshold) {
                        r[0]++;
                    } else {
                        r[1]++;
                    }
                }
            }
            mapTotalThresholdRatings.put(ratingThreshold, r);
        }
        return r;
    }

    public int getNumberOfRatings() {
        return getNumberOfRatings(model, mapTotalRatings);
    }

    public static <U, I, C extends ContextIF> int getNumberOfRatings(final ModelIF<U, I, C> model, final Map<Integer, Double> mapTotalRatings) {
        int r = 0;
        if (mapTotalRatings != null && mapTotalRatings.containsKey(NUMBER_RATINGS)) {
            r = mapTotalRatings.get(NUMBER_RATINGS).intValue();
        } else {
            for (U u : model.getUsers()) {
                r += model.getPreferencesFromUser(u).size();
            }
            if (mapTotalRatings != null) {
                mapTotalRatings.put(NUMBER_RATINGS, 1.0 * r);
            }
        }
        return r;
    }

    public void clearAll() {
        mapMinUserRating.clear();
        mapMaxUserRating.clear();
        mapMinItemRating.clear();
        mapMaxItemRating.clear();
        mapItemRatingByStdNorm.clear();
        mapUserRatingByStdNorm.clear();
        mapAvgUserRating.clear();
        mapAvgItemRating.clear();
        mapTotalRatings.clear();
        mapTotalThresholdRatings.clear();
    }
}