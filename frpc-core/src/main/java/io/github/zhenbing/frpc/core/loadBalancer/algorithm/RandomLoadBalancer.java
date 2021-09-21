package io.github.zhenbing.frpc.core.loadBalancer.algorithm;

import io.github.zhenbing.frpc.core.loadBalancer.AbstractLoadBalancer;
import io.github.zhenbing.frpc.repository.common.ServiceDesc;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

/**
 * RandomLoadBalancer
 *
 * @author fengzhenbing
 */
@Slf4j
public class RandomLoadBalancer extends AbstractLoadBalancer {
    private static final Random RANDOM = new Random();

    @Override
    public ServiceDesc select(List<ServiceDesc> serviceDescList) {
        int totalWeight = calculateTotalWeight(serviceDescList);
        boolean sameWeight = isAllUpStreamSameWeight(serviceDescList);
        if (totalWeight > 0 && !sameWeight) {
            return random(totalWeight, serviceDescList);
        }
        // If the weights are the same or the weights are 0 then random
        int index = RANDOM.nextInt(serviceDescList.size());
        ServiceDesc serviceDesc = serviceDescList.get(index);
        log.info("select index -> {},url -> {}", index, serviceDesc.httpUrl());
        return serviceDesc;
    }

    private boolean isAllUpStreamSameWeight(List<ServiceDesc> serviceDescList) {
        boolean sameWeight = true;
        int length = serviceDescList.size();
        for (int i = 0; i < length; i++) {
            int weight = getWeight(serviceDescList.get(i));
            if (i > 0 && weight != getWeight(serviceDescList.get(i-1))) {
                // Calculate whether the weight of ownership is the same
                sameWeight = false;
                break;
            }
        }
        return sameWeight;
    }

    private int calculateTotalWeight(List<ServiceDesc> serviceDescList) {
        // total weight
        int totalWeight = 0;
        for (ServiceDesc serviceDesc : serviceDescList) {
            totalWeight += getWeight(serviceDesc);
        }
        return totalWeight;
    }


    private ServiceDesc random(final int totalWeight, final List<ServiceDesc> serviceDescList) {
        // If the weights are not the same and the weights are greater than 0, then random by the total number of weights
        int offset = RANDOM.nextInt(totalWeight);
        // Determine which segment the random value falls on
        for (ServiceDesc serviceDesc : serviceDescList) {
            offset -= getWeight(serviceDesc);
            if (offset < 0) {
                return serviceDesc;
            }
        }
        return serviceDescList.get(0);
    }

    @Override
    public String getType() {
        return "random";
    }
}
