// Bridge symbol export for KNOI
// KNOI's init() uses dlsym to find this symbol in libshared.so
// Kotlin/Native's @CName doesn't export to ELF dynamic symbol table,
// so we define it in C to ensure dlsym can find it.

void com_tencent_tmm_knoi_initBridge(void) {
    // This function is called by KNOI's init() after setup("libshared.so").
    // The actual initialization (initBase, service proxy registration) is
    // handled by the KNOI framework itself once this symbol is found.
    // This empty implementation exists solely to provide the symbol.
}
