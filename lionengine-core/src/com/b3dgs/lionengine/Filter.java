package com.b3dgs.lionengine;

/**
 * List of supported filters.
 * <p>
 * Note: HQ3X has to scale the height reference by 3 (if reference is 320*240, final size has to be 960*720 or
 * 1280*720).
 * </p>
 */
public enum Filter
{
    /** No filter. */
    NONE,
    /** Bilinear filtering, blurring pixels. */
    BILINEAR,
    /** hq2x filtering, giving a special result as a painted image (may be slow !). */
    HQ2X,
    /** hq3x filtering, giving a special result as a painted image (may be slow !). */
    HQ3X;
}
