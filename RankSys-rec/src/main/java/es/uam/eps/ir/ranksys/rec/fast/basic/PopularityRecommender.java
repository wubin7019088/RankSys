/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.rec.fast.basic;

import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.rec.fast.AbstractFastRecommender;
import static java.util.Collections.sort;
import java.util.List;
import java.util.function.IntPredicate;
import static java.util.stream.Collectors.toList;

/**
 * Popularity-based recommender. Non-personalized recommender that returns the
 * most popular items according to the preference data provided.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class PopularityRecommender<U, I> extends AbstractFastRecommender<U, I> {

    private final List<IdxDouble> popList;

    /**
     * Constructor.
     *
     * @param data preference data
     */
    public PopularityRecommender(FastPreferenceData<U, I, ?> data) {
        super(data, data);

        popList = data.getIidxWithPreferences()
                .mapToObj(iidx -> new IdxDouble(iidx, (double) data.numUsers(iidx)))
                .collect(toList());
        sort(popList, (p1, p2) -> Double.compare(p2.v, p1.v));
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        if (maxLength == 0) {
            maxLength = popList.size();
        }
        
        List<IdxDouble> items = popList.stream()
                .filter(is -> filter.test(is.idx))
                .limit(maxLength)
                .collect(toList());
        
        return new FastRecommendation(uidx, items);
    }
}
