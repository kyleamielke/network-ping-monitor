import { useMutation } from '@apollo/client';
import { GENERATE_REPORT } from '@/features/reports/api/reportMutations';
import { ReportRequest, ReportResponse } from '@/features/reports/types/report.types';

interface UseReportGenerationResult {
  generateReport: (input: ReportRequest) => Promise<ReportResponse>;
  loading: boolean;
  error: any;
  report: ReportResponse | null;
}

export const useReportGeneration = (): UseReportGenerationResult => {
  const [generateReportMutation, { loading, error, data }] = useMutation(GENERATE_REPORT);

  const generateReport = async (input: ReportRequest): Promise<ReportResponse> => {
    // Convert datetime-local input to ISO format
    const formattedInput = {
      ...input,
      startDate: input.startDate ? new Date(input.startDate).toISOString().slice(0, 19) : undefined,
      endDate: input.endDate ? new Date(input.endDate).toISOString().slice(0, 19) : undefined,
    };

    const result = await generateReportMutation({
      variables: { input: formattedInput },
    });

    return result.data.generateReport;
  };

  return {
    generateReport,
    loading,
    error,
    report: data?.generateReport || null,
  };
};