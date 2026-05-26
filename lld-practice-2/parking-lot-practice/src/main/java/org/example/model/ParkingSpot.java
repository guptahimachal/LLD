package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSpot {

    private Boolean isEmpty = true; // Initialize to true by default
    private String id;
    private Boolean isChargable;
    private ParkingSpotType parkingSpotType;
    
    // 1. ReentrantLock used for Pessimistic Locking
    private final Lock pessimisticLock = new ReentrantLock();
    
    // 2. StampedLock used for Optimistic Locking
    private final StampedLock stampedLock = new StampedLock();

    public enum ParkingSpotType {
        SMALL,
        MID,
        LARGE
    }

    public boolean isVehicleCompatible(Vehicle vehicle) {
        if (vehicle.getIsElectric() && !isChargable) {
            return false;
        }

        switch (vehicle.getVehicleType()) {
            case CAR -> {
                return ParkingSpotType.MID.equals(parkingSpotType);
            }
            case TWO_WHEELER -> {
                return ParkingSpotType.SMALL.equals(parkingSpotType);
            }
            case TRUCK -> {
                return ParkingSpotType.LARGE.equals(parkingSpotType);
            }
        }
        return false;
    }

    // ==========================================
    // PESSIMISTIC APPROACH (ReentrantLock)
    // ==========================================
    public boolean bookPessimistic() {
        pessimisticLock.lock();
        try {
            if (isEmpty) {
                isEmpty = false;
                return true;
            }
            return false;
        } finally {
            pessimisticLock.unlock();
        }
    }

    public void freePessimistic() {
        pessimisticLock.lock();
        try {
            isEmpty = true;
        } finally {
            pessimisticLock.unlock();
        }
    }

    // ==========================================
    // OPTIMISTIC APPROACH (StampedLock)
    // ==========================================
    public boolean bookOptimistic() {
        // 1. Attempt an optimistic read
        long stamp = stampedLock.tryOptimisticRead();
        boolean currentEmpty = isEmpty;

        // 2. If it appears empty, try to acquire a write lock safely
        if (currentEmpty) {
            if (stampedLock.validate(stamp)) {
                // The state hasn't changed since we read it. Let's try to upgrade to a write lock.
                long writeStamp = stampedLock.tryConvertToWriteLock(stamp);
                if (writeStamp != 0L) {
                    try {
                        isEmpty = false;
                        return true;
                    } finally {
                        stampedLock.unlockWrite(writeStamp);
                    }
                }
            }
        }
        return false;
    }

    public void freeOptimistic() {
        long stamp = stampedLock.writeLock();
        try {
            isEmpty = true;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

}
